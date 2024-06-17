package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ResponseFeedbackListDTO {
    private int statusCode;
    private String message;
    private FeedbackData data;

    @Data
    @AllArgsConstructor
    public static class FeedbackData {
        private List<FeedbackInfo> feedbackList;
    }

    @Data
    @AllArgsConstructor
    public static class FeedbackInfo {
        private Long feedbackId;
        private Long exerciseId;
        private Date createdDate;
    }
}

