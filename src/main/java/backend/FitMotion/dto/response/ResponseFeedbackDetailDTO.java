package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseFeedbackDetailDTO {
    private int statusCode;
    private String message;
    private FeedbackData data;

    @Data
    @AllArgsConstructor
    public static class FeedbackData {
        private Long feedbackId;
        private Long exerciseId;
        private String videoUrl;
        private Date createdDate;
    }
}
