package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseFeedbackListDTO {
    private int statusCode;
    private String message;
    private FeedbackData data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackData {
        private List<FeedbackInfo> feedbackList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FeedbackInfo {
        private Long feedbackId;
        private ResponseExerciseDetailDTO exerciseDetail;
        private Long exerciseId;
        private Date createdDate;
    }
}

