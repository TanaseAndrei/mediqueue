package ro.mediqueue.api.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.clinic.domain.Clinic;

import java.util.Optional;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    Optional<Clinic> findBySlugAndDeletedAtIsNull(String slug);

    boolean existsBySlug(String slug);
}
