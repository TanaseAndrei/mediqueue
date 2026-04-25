package ro.mediqueue.api.appointment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotCalculatorService {

    // TODO: inject WorkingHoursRepository, BreakRepository, AppointmentRepository, BlockedSlotRepository

    /**
     * Returns available booking slots for a given clinic and date.
     * Algorithm:
     *  1. Load WorkingHours for the day-of-week
     *  2. Generate all slot windows of slotDurationMin within working hours
     *  3. Remove slots that overlap any Break for that day
     *  4. Remove slots that overlap any CONFIRMED Appointment
     *  5. Remove slots that overlap any BlockedSlot
     */
    // TODO: getAvailableSlots(Long clinicId, LocalDate date)

    // TODO: isSlotAvailable(Long clinicId, OffsetDateTime startsAt, OffsetDateTime endsAt)
}
