package ro.mediqueue.api.clinic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.mediqueue.api.common.domain.BaseEntity;

import java.time.LocalTime;

@Entity
@Table(name = "working_hours")
@Getter
@Setter
@NoArgsConstructor
public class WorkingHours extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    /**
     * ISO day of week: 0 = Sunday, 1 = Monday, ..., 6 = Saturday
     */
    @Column(name = "day_of_week", nullable = false)
    private short dayOfWeek;

    @Column(name = "is_working", nullable = false)
    private boolean isWorking = true;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}
