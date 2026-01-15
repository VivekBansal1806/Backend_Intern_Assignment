package assignment.backend_intern_assignment;

import assignment.backend_intern_assignment.service.EventIngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ConcurrencyTest {

    @Autowired
    private EventIngestionService ingestionService;

    @Test
    void concurrentIngestionIsThreadSafe() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(10);

        Callable<Void> task = () -> {
            ingestionService.ingest(
                    List.of(TestUtils.buildEvent("E-1000", 1))
            );
            return null;
        };

        List<Callable<Void>> tasks =
                List.of(task, task, task, task, task,
                        task, task, task, task, task);

        assertDoesNotThrow(() -> executor.invokeAll(tasks));
        executor.shutdown();
    }
}
