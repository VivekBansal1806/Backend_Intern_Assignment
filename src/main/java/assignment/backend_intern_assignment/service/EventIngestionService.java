package assignment.backend_intern_assignment.service;

import assignment.backend_intern_assignment.dto.BatchIngestResponse;
import assignment.backend_intern_assignment.dto.EventIngestRequest;

import java.util.List;

public interface EventIngestionService {

    BatchIngestResponse ingest(List<EventIngestRequest> requests);

}
