package assignment.backend_intern_assignment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "events", indexes = {
        @Index(name = "idx_machine_event_time", columnList = "machine_id,event_time"),
        @Index(name = "idx_event_time", columnList = "event_time")
}
)
public class Event {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false, length = 50)
    private String eventId;

    @Column(name = "event_time", nullable = false)
    private Instant eventTime;

    @Column(name = "received_time", nullable = false)
    private Instant receivedTime;

    @Column(name = "machine_id", nullable = false, length = 50)
    private String machineId;

    @Column(name = "duration_ms", nullable = false)
    private long durationMs;

    @Column(name = "defect_count", nullable = false)
    private int defectCount;

    @Column(name = "payload_hash", nullable = false, length = 64)
    private String payloadHash;

}
