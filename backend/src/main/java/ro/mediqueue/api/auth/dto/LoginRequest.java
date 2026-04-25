package ro.mediqueue.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank @Size(max = 80)
        String clinicSlug,

        @NotBlank @Email @Size(max = 160)
        String email,

        @NotBlank @Size(max = 100)
        String password
) {}
