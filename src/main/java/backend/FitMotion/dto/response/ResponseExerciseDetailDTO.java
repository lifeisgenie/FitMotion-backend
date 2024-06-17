package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseExerciseDetailDTO {
    private int statusCode;
    private String message;
    private ExerciseData data;

    @Data
    @AllArgsConstructor
    public static class ExerciseData {
        private String exerciseName;
        private String exerciseCategory;
        private String exerciseExplain;
        private String exerciseUrl;
    }
}
