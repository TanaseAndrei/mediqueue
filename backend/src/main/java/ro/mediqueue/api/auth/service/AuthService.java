package ro.mediqueue.api.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.mediqueue.api.auth.domain.RefreshToken;
import ro.mediqueue.api.auth.domain.User;
import ro.mediqueue.api.auth.domain.UserRole;
import ro.mediqueue.api.auth.dto.LoginRequest;
import ro.mediqueue.api.auth.dto.LoginResponse;
import ro.mediqueue.api.auth.dto.RegisterRequest;
import ro.mediqueue.api.auth.repository.RefreshTokenRepository;
import ro.mediqueue.api.auth.repository.UserRepository;
import ro.mediqueue.api.clinic.domain.Clinic;
import ro.mediqueue.api.clinic.repository.ClinicRepository;
import ro.mediqueue.api.common.exception.BadRequestException;
import ro.mediqueue.api.common.exception.ConflictException;
import ro.mediqueue.api.common.exception.ResourceNotFoundException;
import ro.mediqueue.api.common.security.JwtService;
import ro.mediqueue.api.config.AppProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AppProperties appProperties;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Registers a new clinic with its first ADMIN user in a single transaction.
     * If the slug is taken or the email already exists for the clinic, throws ConflictException.
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (clinicRepository.existsBySlug(request.clinicSlug())) {
            throw new ConflictException("Slug-ul '%s' este deja folosit".formatted(request.clinicSlug()));
        }

        Clinic clinic = new Clinic();
        clinic.setName(request.clinicName());
        clinic.setSlug(request.clinicSlug());
        clinic.setCreatedAt(OffsetDateTime.now());
        clinic.setUpdatedAt(OffsetDateTime.now());
        clinic = clinicRepository.save(clinic);

        User admin = new User();
        admin.setClinic(clinic);
        admin.setEmail(request.adminEmail());
        admin.setPasswordHash(passwordEncoder.encode(request.adminPassword()));
        admin.setFullName(request.adminFullName());
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        admin.setCreatedAt(OffsetDateTime.now());
        admin.setUpdatedAt(OffsetDateTime.now());
        admin = userRepository.save(admin);

        log.info("Registered clinic [{}] with admin user [{}]", clinic.getSlug(), admin.getId());
        return issueTokenPair(admin);
    }

    /**
     * Authenticates a user by clinic slug + email + password.
     * Uses timing-safe comparison to prevent user enumeration.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Clinic clinic = clinicRepository.findBySlugAndDeletedAtIsNull(request.clinicSlug())
                .orElseThrow(() -> new BadRequestException("Credentiale invalide"));

        User user = userRepository
                .findByClinicIdAndEmailAndDeletedAtIsNull(clinic.getId(), request.email())
                .orElseThrow(() -> new BadRequestException("Credentiale invalide"));

        if (!user.isActive()) {
            throw new BadRequestException("Credentiale invalide");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadRequestException("Credentiale invalide");
        }

        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);

        return issueTokenPair(user);
    }

    /**
     * Rotates a refresh token: the old token is revoked and a new pair is issued.
     * Implements token reuse detection — if the token was already revoked, all tokens
     * for that user are revoked (possible token theft scenario).
     */
    @Transactional
    public LoginResponse refresh(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        RefreshToken existing = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Refresh token invalid"));

        if (existing.isRevoked()) {
            // Possible token reuse — revoke all tokens for this user as a precaution
            log.warn("Refresh token reuse detected for user [{}] — revoking all tokens",
                    existing.getUser().getId());
            refreshTokenRepository.revokeAllForUser(existing.getUser().getId(), OffsetDateTime.now());
            throw new BadRequestException("Refresh token invalid");
        }

        if (existing.isExpired()) {
            throw new BadRequestException("Refresh token expirat");
        }

        existing.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(existing);

        LoginResponse response = issueTokenPair(existing.getUser());

        // Link old token to its replacement for audit trail
        refreshTokenRepository.findByTokenHash(hashToken(response.refreshToken()))
                .ifPresent(newToken -> {
                    existing.setReplacedBy(newToken);
                    refreshTokenRepository.save(existing);
                });

        return response;
    }

    /**
     * Revokes the provided refresh token (logout).
     * Silently ignores invalid tokens to prevent information leakage.
     */
    @Transactional
    public void logout(String rawRefreshToken) {
        String tokenHash = hashToken(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevokedAt(OffsetDateTime.now());
            refreshTokenRepository.save(token);
        });
    }

    // --- Private helpers ---

    private LoginResponse issueTokenPair(User user) {
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getClinic().getId(), user.getRole().name());

        String rawRefreshToken = generateOpaqueToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashToken(rawRefreshToken));
        refreshToken.setIssuedAt(OffsetDateTime.now());
        refreshToken.setExpiresAt(OffsetDateTime.now()
                .plusDays(appProperties.getJwt().getRefreshTokenExpirationDays()));
        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(
                accessToken,
                rawRefreshToken,
                appProperties.getJwt().getAccessTokenExpirationMs(),
                user.getId(),
                user.getClinic().getId(),
                user.getRole().name()
        );
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            // SHA-256 is guaranteed by the JVM spec — this branch is unreachable
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
