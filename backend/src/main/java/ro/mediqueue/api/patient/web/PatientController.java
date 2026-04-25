package ro.mediqueue.api.patient.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Vizualizarea istoricului pacientilor (autenticat)")
public class PatientController {

    // TODO: GET /  — search patients by phone/email within the authenticated clinic
    // TODO: GET /{phone}/appointments — appointment history for a patient
}
