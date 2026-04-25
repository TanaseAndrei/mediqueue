package ro.mediqueue.api.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ro.mediqueue.api.appointment.domain.Appointment;
import ro.mediqueue.api.appointment.domain.AppointmentStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByCancellationToken(UUID cancellationToken);

    List<Appointment> findByClinicIdAndStatusAndStartsAtBetweenOrderByStartsAt(
            Long clinicId,
            AppointmentStatus status,
            OffsetDateTime from,
            OffsetDateTime to
    );

    /**
     * Checks for overlapping CONFIRMED appointments in a clinic within the given range.
     * Used as a double-check before insert — the DB exclusion constraint is the final guard.
     */
    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.clinic.id = :clinicId
              AND a.status = 'CONFIRMED'
              AND a.startsAt < :endsAt
              AND a.endsAt > :startsAt
            """)
    boolean existsOverlap(Long clinicId, OffsetDateTime startsAt, OffsetDateTime endsAt);
}
