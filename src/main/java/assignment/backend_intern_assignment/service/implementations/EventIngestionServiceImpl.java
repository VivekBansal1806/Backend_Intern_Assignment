package assignment.backend_intern_assignment.service.implementations;

import assignment.backend_intern_assignment.dto.BatchIngestResponse;
import assignment.backend_intern_assignment.dto.EventIngestRequest;
import assignment.backend_intern_assignment.dto.EventRejection;
import assignment.backend_intern_assignment.entity.Event;
import assignment.backend_intern_assignment.repository.EventRepo;
import assignment.backend_intern_assignment.service.EventIngestionService;
import assignment.backend_intern_assignment.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EventIngestionServiceImpl implements EventIngestionService {

    private static final long MAX_DURATION_MS = 6 * 60 * 60 * 1000; // 6 hours

    private final EventRepo eventRepo;

    public EventIngestionServiceImpl(EventRepo eventRepo) {
        this.eventRepo = eventRepo;
    }

    @Transactional
    public BatchIngestResponse ingest(List<EventIngestRequest> requests) {

        BatchIngestResponse response = new BatchIngestResponse();
        List<EventRejection> rejections = new ArrayList<>();

        int accepted = 0, deduped = 0, updated = 0, rejected = 0;

        for (EventIngestRequest req : requests) {

            // -------- Validation --------
            String validationError = validate(req);
            if (validationError != null) {
                rejected++;
                rejections.add(new EventRejection(req.getEventId(), validationError));
                continue;
            }

            Instant now = Instant.now();
            String payloadHash = HashUtil.computeHash(req);

            Optional<Event> existingOpt = eventRepo.findById(req.getEventId());

            if (existingOpt.isEmpty()) {
                // -------- Insert --------
                Event event = buildEvent(req, payloadHash, now);
                eventRepo.save(event);
                accepted++;
                continue;
            }

            Event existing = existingOpt.get();

            // -------- Deduplication --------
            if (existing.getPayloadHash().equals(payloadHash)) {
                deduped++;
                continue;
            }

            // -------- Update logic --------
            if (now.isAfter(existing.getReceivedTime())) {
                existing.setEventTime(req.getEventTime());
                existing.setMachineId(req.getMachineId());
                existing.setDurationMs(req.getDurationMs());
                existing.setDefectCount(req.getDefectCount());
                existing.setPayloadHash(payloadHash);
                existing.setReceivedTime(now);

                eventRepo.save(existing);
                updated++;
            } else {
                deduped++;
            }
        }

        response.setAccepted(accepted);
        response.setUpdated(updated);
        response.setDeduped(deduped);
        response.setRejected(rejected);
        response.setRejections(rejections);

        return response;
    }

    private String validate(EventIngestRequest req) {

        if (req.getDurationMs() < 0 || req.getDurationMs() > MAX_DURATION_MS) {
            return "INVALID_DURATION";
        }

        if (req.getEventTime().isAfter(Instant.now().plusSeconds(15 * 60))) {
            return "EVENT_TIME_IN_FUTURE";
        }

        return null;
    }

    private Event buildEvent(EventIngestRequest req, String hash, Instant now) {

        Event event = new Event();
        event.setEventId(req.getEventId());
        event.setEventTime(req.getEventTime());
        event.setMachineId(req.getMachineId());
        event.setDurationMs(req.getDurationMs());
        event.setDefectCount(req.getDefectCount());
        event.setPayloadHash(hash);
        event.setReceivedTime(now);

        return event;
    }
}
