package assignment.backend_intern_assignment.service.implementations;

import assignment.backend_intern_assignment.dto.MachineStatsResponse;
import assignment.backend_intern_assignment.entity.Event;
import assignment.backend_intern_assignment.repository.EventRepo;
import assignment.backend_intern_assignment.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {

    private final EventRepo eventRepo;

    public StatsServiceImpl(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public MachineStatsResponse getMachineStats(String machineId, Instant start, Instant end) {

        List<Event> events = eventRepo.findByMachineIdAndEventTimeBetween(machineId, start, end);

        long eventsCount = events.size();
        long defectsCount = events.stream().filter(e -> e.getDefectCount() != -1).mapToLong(Event::getDefectCount).sum();

        double windowHours = Duration.between(start, end).toSeconds() / 3600.0;

        double avgDefectRate = windowHours == 0 ? 0 : defectsCount / windowHours;

        String status = avgDefectRate < 2.0 ? "Healthy" : "Warning";

        MachineStatsResponse response = new MachineStatsResponse();
        response.setMachineId(machineId);
        response.setStart(start);
        response.setEnd(end);
        response.setEventsCount(eventsCount);
        response.setDefectsCount(defectsCount);
        response.setAvgDefectRate(avgDefectRate);
        response.setStatus(status);

        return response;
    }
}
