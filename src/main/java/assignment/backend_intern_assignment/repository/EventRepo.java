package assignment.backend_intern_assignment.repository;


import assignment.backend_intern_assignment.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, String> {

    List<Event> findByMachineIdAndEventTimeBetween(
            String machineId, Instant start, Instant end);

    @Query("""
                SELECT e
                FROM Event e
                WHERE e.eventTime >= :from
                  AND e.eventTime < :to
            """)
    List<Event> findAllInTimeRange(
            @Param("from") Instant from,
            @Param("to") Instant to);
}