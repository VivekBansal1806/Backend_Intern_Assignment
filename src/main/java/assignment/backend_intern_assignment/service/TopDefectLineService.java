package assignment.backend_intern_assignment.service;


import assignment.backend_intern_assignment.dto.TopDefectLineResponse;

import java.time.Instant;
import java.util.List;

public interface TopDefectLineService {
    List<TopDefectLineResponse> getTopDefectLines(
            String factoryId, Instant from, Instant to, int limit);
}
