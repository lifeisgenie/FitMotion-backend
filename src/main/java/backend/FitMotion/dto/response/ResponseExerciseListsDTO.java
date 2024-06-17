package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResponseExerciseListsDTO {
    private int statusCode;
    private String message;
    private ExerciseData data;

    @Data
    @AllArgsConstructor
    public static class ExerciseData {
        private List<ExerciseInfo> exerciseList;
    }

    @Data
    @AllArgsConstructor
    public static class ExerciseInfo {
        private String exerciseName;
        private String exerciseCategory;
        private String exerciseExplain;
        private String exerciseUrl;
    }
}

