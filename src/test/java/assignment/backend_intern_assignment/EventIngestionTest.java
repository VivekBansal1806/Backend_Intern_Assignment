package assignment.backend_intern_assignment;

import assignment.backend_intern_assignment.dto.BatchIngestResponse;
import assignment.backend_intern_assignment.dto.EventIngestRequest;
import assignment.backend_intern_assignment.service.EventIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EventIngestionTest {

    @Autowired
    private EventIngestionService ingestionService;

    @Test
    void identicalDuplicateEventIsDeduped() {
        EventIngestRequest e = TestUtils.buildEvent("E-1", 0);

        ingestionService.ingest(List.of(e));
        BatchIngestResponse response =
                ingestionService.ingest(List.of(e));

        assertEquals(1, response.getDeduped());
    }

    @Test
    void newerPayloadUpdatesExistingEvent() throws Exception {
        EventIngestRequest e1 = TestUtils.buildEvent("E-2", 1);
        ingestionService.ingest(List.of(e1));

        Thread.sleep(5); // ensure newer receivedTime

        EventIngestRequest e2 = TestUtils.buildEvent("E-2", 3);
        BatchIngestResponse response =
                ingestionService.ingest(List.of(e2));

        assertEquals(1, response.getUpdated());
    }

    @Test
    void invalidDurationIsRejected() {
        EventIngestRequest e = TestUtils.buildEvent("E-3", 1);
        e.setDurationMs(-10);

        BatchIngestResponse response =
                ingestionService.ingest(List.of(e));

        assertEquals(1, response.getRejected());
        assertEquals("INVALID_DURATION",
                response.getRejections().get(0).getReason());
    }

    @Test
    void futureEventTimeIsRejected() {
        EventIngestRequest e = TestUtils.buildEvent("E-4", 1);
        e.setEventTime(Instant.now().plusSeconds(16 * 60));

        BatchIngestResponse response =
                ingestionService.ingest(List.of(e));

        assertEquals(1, response.getRejected());
    }
}
