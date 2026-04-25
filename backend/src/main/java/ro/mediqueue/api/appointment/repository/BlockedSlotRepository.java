package ro.mediqueue.api.appointment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.appointment.domain.BlockedSlot;

import java.time.OffsetDateTime;
import java.util.List;

public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {

    List<BlockedSlot> findByClinicIdAndStartsAtBetweenOrderByStartsAt(
            Long clinicId,
            OffsetDateTime from,
            OffsetDateTime to
    );
}
