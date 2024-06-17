package backend.FitMotion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestFeedbackSaveDTO {
    private String content;
    private String videoUrl;
    private Long userId;
    private Long exerciseId;
}