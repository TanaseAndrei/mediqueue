package ro.mediqueue.api.appointment.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Gestionarea programarilor (autenticat)")
public class AppointmentController {

    // TODO: inject BookingService, CancellationService

    @GetMapping
    @Operation(summary = "Lista programarilor pentru o zi (admin)")
    public Object listAppointments(@RequestParam LocalDate date) {
        // TODO: implement — filter by authenticated user's clinicId from JWT
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // TODO: PATCH /{id}/cancel — cancelByClinic
    // TODO: PATCH /{id}/status — update status (NO_SHOW, COMPLETED)
    // TODO: GET /{id}
    // TODO: POST /blocked-slots
    // TODO: DELETE /blocked-slots/{id}
}
