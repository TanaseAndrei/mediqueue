package ro.mediqueue.api.appointment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mediqueue.api.auth.domain.User;
import ro.mediqueue.api.clinic.domain.Clinic;

import java.time.OffsetDateTime;

/**
 * Blocks a specific time range from being bookable (e.g., vacation, maintenance).
 * Does not extend BaseEntity because it has no updated_at column.
 */
@Entity
@Table(name = "blocked_slots")
@Getter
@Setter
@NoArgsConstructor
public class BlockedSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @Column(name = "starts_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime endsAt;

    @Column(name = "reason", length = 200)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
}
