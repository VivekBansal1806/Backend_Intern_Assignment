package assignment.backend_intern_assignment.service;

import assignment.backend_intern_assignment.dto.MachineStatsResponse;

import java.time.Instant;

public interface StatsService {
    MachineStatsResponse getMachineStats(String machineId, Instant start, Instant end);
}
