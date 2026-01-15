package assignment.backend_intern_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopDefectLineResponse {
    private String lineId;
    private long totalDefects;
    private long eventCount;
    private double defectsPercent;
}
