package ro.mediqueue.api.appointment.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
@Tag(name = "Public Booking", description = "Programari publice (fara autentificare)")
public class PublicBookingController {

    // TODO: inject BookingService, SlotCalculatorService, CancellationService

    @GetMapping("/clinics/{slug}/slots")
    @Operation(summary = "Returneaza slot-urile disponibile pentru o data (public)")
    public Object getAvailableSlots(
            @PathVariable String slug,
            @RequestParam LocalDate date) {
        // TODO: implement — rate limited via Bucket4j
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // TODO: POST /clinics/{slug}/appointments — bookAppointment
    // TODO: GET  /appointments/{cancellationToken} — get appointment by token (for cancellation page)
    // TODO: POST /appointments/{cancellationToken}/cancel — cancelByPatient
}
