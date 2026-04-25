package ro.mediqueue.api.common.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised error handling using RFC 7807 Problem Details.
 * Every response has Content-Type: application/problem+json.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERRORS_FIELD = "errors";

    // --- Validation ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                        (first, second) -> first  // keep first message on duplicate field
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Validarea datelor a esuat");
        problem.setType(URI.create("https://mediqueue.ro/errors/validation-failed"));
        problem.setTitle("Validation Failed");
        problem.setProperty(VALIDATION_ERRORS_FIELD, fieldErrors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> violations = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        cv -> cv.getPropertyPath().toString(),
                        cv -> cv.getMessage(),
                        (first, second) -> first
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Validarea parametrilor a esuat");
        problem.setType(URI.create("https://mediqueue.ro/errors/constraint-violation"));
        problem.setTitle("Constraint Violation");
        problem.setProperty(VALIDATION_ERRORS_FIELD, violations);
        return problem;
    }

    // --- Business Exceptions ---

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setType(URI.create("https://mediqueue.ro/errors/not-found"));
        problem.setTitle("Not Found");
        return problem;
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, ex.getMessage());
        problem.setType(URI.create("https://mediqueue.ro/errors/conflict"));
        problem.setTitle("Conflict");
        return problem;
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(URI.create("https://mediqueue.ro/errors/bad-request"));
        problem.setTitle("Bad Request");
        return problem;
    }

    // --- DB Integrity Violations ---

    /**
     * Catches unique-constraint and exclusion-constraint violations from PostgreSQL.
     * The no_overlap_confirmed exclusion constraint maps to HTTP 409.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        String detail = "Operatia incalca o constrangere de integritate a datelor";
        String causeMsg = ex.getMostSpecificCause().getMessage();

        if (causeMsg != null && causeMsg.contains("no_overlap_confirmed")) {
            detail = "Intervalul solicitat se suprapune cu o programare existenta";
        } else if (causeMsg != null && causeMsg.contains("uq_")) {
            detail = "Resursa exista deja";
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, detail);
        problem.setType(URI.create("https://mediqueue.ro/errors/conflict"));
        problem.setTitle("Conflict");
        return problem;
    }

    // --- Security ---

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Autentificare invalida");
        problem.setType(URI.create("https://mediqueue.ro/errors/unauthorized"));
        problem.setTitle("Unauthorized");
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Nu aveti permisiunea necesara");
        problem.setType(URI.create("https://mediqueue.ro/errors/forbidden"));
        problem.setTitle("Forbidden");
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request body invalid sau lipsa");
        problem.setType(URI.create("https://mediqueue.ro/errors/bad-request"));
        problem.setTitle("Bad Request");
        return problem;
    }

    // --- Catch-all ---

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex, WebRequest request) {
        log.error("Unhandled exception on {}: {}", request.getDescription(false), ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "A aparut o eroare interna. Va rugam reincercati.");
        problem.setType(URI.create("https://mediqueue.ro/errors/internal-error"));
        problem.setTitle("Internal Server Error");
        return problem;
    }
}
