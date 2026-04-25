package ro.mediqueue.api.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.clinic.domain.Break;

import java.util.List;

public interface BreakRepository extends JpaRepository<Break, Long> {

    List<Break> findByClinicIdAndDayOfWeekOrderByStartTime(Long clinicId, short dayOfWeek);
}
