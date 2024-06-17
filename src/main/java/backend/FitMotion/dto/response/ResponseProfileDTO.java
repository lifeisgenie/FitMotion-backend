package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseProfileDTO {
    private int statusCode;
    private String message;
    private userProfile data;
    @Data
    @AllArgsConstructor
    public static class userProfile{
        private Long userId;
        private String username;
        private int age;
        private String phone;
        private double height;
        private double weight;
    }
}
