package assignment.backend_intern_assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchIngestResponse {
    private int accepted;
    private int deduped;
    private int updated;
    private int rejected;
    private List<EventRejection> rejections;
}
