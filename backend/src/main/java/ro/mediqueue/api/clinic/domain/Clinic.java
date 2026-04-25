package ro.mediqueue.api.clinic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mediqueue.api.common.domain.BaseEntity;

import java.time.OffsetDateTime;

@Entity
@Table(name = "clinics")
@Getter
@Setter
@NoArgsConstructor
public class Clinic extends BaseEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * URL-friendly identifier used in the public booking link: /book/{slug}
     */
    @Column(name = "slug", nullable = false, unique = true, length = 80)
    private String slug;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone = "Europe/Bucharest";

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 160)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    /**
     * Duration of a single bookable slot in minutes. Constrained between 5 and 240 at DB level.
     */
    @Column(name = "slot_duration_min", nullable = false)
    private short slotDurationMin = 30;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime deletedAt;
}
