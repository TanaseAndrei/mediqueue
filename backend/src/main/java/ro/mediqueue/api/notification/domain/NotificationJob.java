package ro.mediqueue.api.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mediqueue.api.appointment.domain.Appointment;
import ro.mediqueue.api.clinic.domain.Clinic;
import ro.mediqueue.api.common.domain.BaseEntity;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notification_jobs")
@Getter
@Setter
@NoArgsConstructor
public class NotificationJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private NotificationStatus status = NotificationStatus.PENDING;

    @Column(name = "recipient", nullable = false, length = 160)
    private String recipient;

    /**
     * JSONB payload containing template variables for the notification body.
     * Stored as a raw JSON string; deserialisation is deferred to the sender.
     */
    @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
    private String payload;

    @Column(name = "send_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime sendAt;

    @Column(name = "sent_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime sentAt;

    @Column(name = "attempts", nullable = false)
    private short attempts = 0;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
}
