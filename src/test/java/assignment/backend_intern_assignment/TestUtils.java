package assignment.backend_intern_assignment;

import assignment.backend_intern_assignment.dto.EventIngestRequest;

import java.time.Instant;

public class TestUtils {

    public static EventIngestRequest buildEvent(String eventId, int defectCount) {
        EventIngestRequest event = new EventIngestRequest();
        event.setEventId(eventId);
        event.setMachineId("M-1");
        event.setEventTime(Instant.now());
        event.setDurationMs(1000);
        event.setDefectCount(defectCount);
        return event;
    }
}
