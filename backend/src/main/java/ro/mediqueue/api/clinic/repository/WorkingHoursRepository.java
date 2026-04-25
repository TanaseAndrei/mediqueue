package ro.mediqueue.api.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.clinic.domain.WorkingHours;

import java.util.List;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    List<WorkingHours> findByClinicIdOrderByDayOfWeek(Long clinicId);
}
