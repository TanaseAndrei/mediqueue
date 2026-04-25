package ro.mediqueue.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Payload for registering a new clinic together with its first ADMIN user.
 */
public record RegisterRequest(

        @NotBlank @Size(max = 200)
        String clinicName,

        @NotBlank @Size(max = 80)
        @Pattern(regexp = "^[a-z0-9-]+$",
                 message = "Slug-ul poate contine doar litere mici, cifre si cratime")
        String clinicSlug,

        @NotBlank @Size(max = 160)
        String adminFullName,

        @NotBlank @Email @Size(max = 160)
        String adminEmail,

        @NotBlank @Size(min = 8, max = 100)
        String adminPassword
) {}
