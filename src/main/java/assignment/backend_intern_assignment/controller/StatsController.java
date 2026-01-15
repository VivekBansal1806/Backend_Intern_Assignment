package assignment.backend_intern_assignment.controller;

import assignment.backend_intern_assignment.dto.MachineStatsResponse;
import assignment.backend_intern_assignment.dto.TopDefectLineResponse;
import assignment.backend_intern_assignment.service.StatsService;
import assignment.backend_intern_assignment.service.TopDefectLineService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;
    private final TopDefectLineService topDefectLineService;


    public StatsController(StatsService statsService,TopDefectLineService topDefectLineService) {
        this.statsService = statsService;
        this.topDefectLineService = topDefectLineService;

    }

    @GetMapping
    public ResponseEntity<MachineStatsResponse> getMachineStats(
            @RequestParam String machineId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {

        if (machineId == null || machineId.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (start == null || end == null || !end.isAfter(start)) {
            return ResponseEntity.badRequest().build();
        }
        MachineStatsResponse response =
                statsService.getMachineStats(machineId, start, end);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-defect-lines")
    public ResponseEntity<List<TopDefectLineResponse>> getTopDefectLines(
            @RequestParam String factoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "10") int limit) {

        if (from == null || to == null || !to.isAfter(from)) {
            return ResponseEntity.badRequest().build();
        }

        if (limit < 1) {
            limit = 10; // Default limit
        }

        List<TopDefectLineResponse> response =
                topDefectLineService.getTopDefectLines(factoryId, from, to, limit);

        return ResponseEntity.ok(response);
    }
}
