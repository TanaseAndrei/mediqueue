package ro.mediqueue.api.appointment.domain;

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
import ro.mediqueue.api.clinic.domain.Clinic;
import ro.mediqueue.api.common.domain.BaseEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    /**
     * Opaque token sent to the patient to allow self-service cancellation
     * without requiring an account.
     */
    @Column(name = "cancellation_token", nullable = false, unique = true,
            columnDefinition = "UUID")
    private UUID cancellationToken;

    @Column(name = "patient_name", nullable = false, length = 160)
    private String patientName;

    @Column(name = "patient_email", nullable = false, length = 160)
    private String patientEmail;

    @Column(name = "patient_phone", nullable = false, length = 32)
    private String patientPhone;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "starts_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @Column(name = "cancelled_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime cancelledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by", length = 20)
    private CancelledBy cancelledBy;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;
}
