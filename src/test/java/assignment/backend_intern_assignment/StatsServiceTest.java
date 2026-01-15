package assignment.backend_intern_assignment;

import assignment.backend_intern_assignment.dto.MachineStatsResponse;
import assignment.backend_intern_assignment.repository.EventRepo;
import assignment.backend_intern_assignment.service.EventIngestionService;
import assignment.backend_intern_assignment.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class StatsServiceTest {

    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private EventIngestionService ingestionService;

    @Autowired
    private StatsService statsService;

    @BeforeEach
    void cleanDatabase() {
        eventRepo.deleteAll();
    }


    @Test
    void defectMinusOneIsIgnored() {
        ingestionService.ingest(List.of(
                TestUtils.buildEvent("E-5", -1),
                TestUtils.buildEvent("E-6", 2)
        ));

        MachineStatsResponse stats =
                statsService.getMachineStats(
                        "M-1",
                        Instant.now().minusSeconds(3600),
                        Instant.now().plusSeconds(10)
                );

        assertEquals(2, stats.getEventsCount());
        assertEquals(2, stats.getDefectsCount());
    }

    @Test
    void startInclusiveEndExclusive() {
        Instant start = Instant.now();

        var e = TestUtils.buildEvent("E-7", 1);
        e.setEventTime(start);

        ingestionService.ingest(List.of(e));

        MachineStatsResponse stats =
                statsService.getMachineStats(
                        "M-1",
                        start,
                        start.plusSeconds(1)
                );

        assertEquals(1, stats.getEventsCount());
    }

    @Test
    void healthStatusIsCalculatedCorrectly() {
        ingestionService.ingest(List.of(
                TestUtils.buildEvent("E-8", 10)
        ));

        MachineStatsResponse stats =
                statsService.getMachineStats(
                        "M-1",
                        Instant.now().minusSeconds(3600),
                        Instant.now().plusSeconds(10)
                );

        assertEquals("Warning", stats.getStatus());
    }
}
