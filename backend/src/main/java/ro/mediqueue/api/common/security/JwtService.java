package ro.mediqueue.api.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.mediqueue.api.config.AppProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final String CLAIM_CLINIC_ID = "clinicId";
    private static final String CLAIM_ROLE = "role";

    private final AppProperties appProperties;

    /**
     * Generates a signed access token.
     * Subject = userId as string; extra claims carry clinicId and role.
     */
    public String generateAccessToken(long userId, long clinicId, String role) {
        long nowMs = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim(CLAIM_CLINIC_ID, clinicId)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(nowMs))
                .expiration(new Date(nowMs + appProperties.getJwt().getAccessTokenExpirationMs()))
                .signWith(signingKey())
                .compact();
    }

    /**
     * Parses and validates a token. Returns empty Optional on any failure
     * so the caller does not have to catch JwtException.
     */
    public Optional<Claims> validateAndParseClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public Optional<Long> extractUserId(String token) {
        return validateAndParseClaims(token)
                .map(claims -> Long.parseLong(claims.getSubject()));
    }

    public Optional<Long> extractClinicId(String token) {
        return validateAndParseClaims(token)
                .map(claims -> claims.get(CLAIM_CLINIC_ID, Long.class));
    }

    private SecretKey signingKey() {
        byte[] keyBytes = appProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
