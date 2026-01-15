package assignment.backend_intern_assignment.service.implementations;

import assignment.backend_intern_assignment.dto.TopDefectLineResponse;
import assignment.backend_intern_assignment.entity.Event;
import assignment.backend_intern_assignment.repository.EventRepo;
import assignment.backend_intern_assignment.service.TopDefectLineService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TopDefectLineServiceImpl implements TopDefectLineService {

    private final EventRepo eventRepo;

    public TopDefectLineServiceImpl(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    public List<TopDefectLineResponse> getTopDefectLines(
            String factoryId, Instant from, Instant to, int limit) {

        List<Event> events = eventRepo.findAllInTimeRange(from, to);

        Map<String, List<Event>> grouped =
                events.stream()
                        .collect(Collectors.groupingBy(Event::getMachineId));

        return grouped.entrySet()
                .stream()
                .map(entry -> buildResponse(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingLong(TopDefectLineResponse::getTotalDefects).reversed())
                .limit(limit)
                .toList();
    }

    private TopDefectLineResponse buildResponse(String lineId, List<Event> events) {

        long eventCount = events.size();

        long totalDefects = events.stream()
                .filter(e -> e.getDefectCount() != -1)
                .mapToLong(Event::getDefectCount)
                .sum();

        double defectsPercent =
                eventCount == 0 ? 0 :
                        Math.round((totalDefects * 100.0 / eventCount) * 100.0) / 100.0;

        TopDefectLineResponse response = new TopDefectLineResponse();
        response.setLineId(lineId);
        response.setEventCount(eventCount);
        response.setTotalDefects(totalDefects);
        response.setDefectsPercent(defectsPercent);

        return response;
    }
}
