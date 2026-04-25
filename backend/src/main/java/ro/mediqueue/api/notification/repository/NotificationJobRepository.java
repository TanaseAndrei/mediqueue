package ro.mediqueue.api.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mediqueue.api.notification.domain.NotificationJob;
import ro.mediqueue.api.notification.domain.NotificationStatus;

import java.time.OffsetDateTime;
import java.util.List;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, Long> {

    List<NotificationJob> findByStatusAndSendAtBeforeOrderBySendAt(
            NotificationStatus status,
            OffsetDateTime before
    );
}
