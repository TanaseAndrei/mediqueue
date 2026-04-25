package ro.mediqueue.api.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancellationService {

    // TODO: inject AppointmentRepository, NotificationJobRepository

    // TODO: cancelByPatient(UUID cancellationToken, String reason)
    //       - find appointment by token
    //       - set status=CANCELLED, cancelledBy=PATIENT, cancelledAt=now()
    //       - enqueue CANCELLATION notification to clinic

    // TODO: cancelByClinic(Long clinicId, Long appointmentId, String reason)
    //       - set status=CANCELLED, cancelledBy=CLINIC
    //       - enqueue CANCELLATION notification to patient
}
