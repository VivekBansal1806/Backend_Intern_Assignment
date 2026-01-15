package assignment.backend_intern_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventIngestRequest {
    private String eventId;
    private Instant eventTime;
    private String machineId;
    private long durationMs;
    private int defectCount;
}
