package ro.mediqueue.api.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.auth.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByClinicIdAndEmailAndDeletedAtIsNull(Long clinicId, String email);

    boolean existsByClinicIdAndEmail(Long clinicId, String email);
}
