package assignment.backend_intern_assignment.controller;

import assignment.backend_intern_assignment.dto.BatchIngestRequest;
import assignment.backend_intern_assignment.dto.BatchIngestResponse;
import assignment.backend_intern_assignment.service.EventIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventIngestionController {

    private final EventIngestionService ingestionService;

    public EventIngestionController(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/batch")
    public ResponseEntity<BatchIngestResponse> ingestBatch(@RequestBody @Valid BatchIngestRequest request) {

        BatchIngestResponse response = ingestionService.ingest(request.getEvents());
        return ResponseEntity.ok(response);
    }
}