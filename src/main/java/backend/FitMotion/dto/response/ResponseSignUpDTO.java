package backend.FitMotion.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseSignUpDTO {
    private String email;
    private String username;
    private int age;
    private String phone;
    private int height;
    private int weight;
}
