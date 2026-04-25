package ro.mediqueue.api.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    // TODO: inject AppointmentRepository, ClinicRepository, SlotCalculatorService, NotificationJobRepository

    // TODO: bookAppointment(String clinicSlug, BookAppointmentRequest request)
    //       - validate slot is within working hours and not in a break
    //       - call SlotCalculatorService to confirm slot availability
    //       - persist Appointment
    //       - enqueue CONFIRMATION notification job
    //       - enqueue REMINDER_24H and REMINDER_2H notification jobs

    // TODO: getAppointments(Long clinicId, LocalDate date) — admin view
}
