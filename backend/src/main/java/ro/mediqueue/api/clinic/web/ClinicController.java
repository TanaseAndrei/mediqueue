package ro.mediqueue.api.clinic.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clinics")
@RequiredArgsConstructor
@Tag(name = "Clinic", description = "Gestionarea configuratiei cabinetului")
public class ClinicController {

    // TODO: inject ClinicService

    @GetMapping("/{id}")
    @Operation(summary = "Obtine detaliile unui cabinet (autenticat)")
    public Object getClinic(@PathVariable Long id) {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // TODO: PUT /{id} — updateClinic
    // TODO: GET /{id}/working-hours
    // TODO: PUT /{id}/working-hours
    // TODO: GET /{id}/breaks
    // TODO: POST /{id}/breaks
    // TODO: DELETE /{id}/breaks/{breakId}
}
