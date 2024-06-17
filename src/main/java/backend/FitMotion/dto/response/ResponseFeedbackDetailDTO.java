package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
public class ResponseFeedbackDetailDTO {
    private int statusCode;
    private String message;
    private FeedbackData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackData {
        private Long feedbackId;
        private String content;
        private String videoUrl;
        private Long exerciseId;
        private Long userId;
        private Date createdDate;
    }
}
