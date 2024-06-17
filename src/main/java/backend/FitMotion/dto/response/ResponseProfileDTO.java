package backend.FitMotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProfileDTO {
    private Long userId;
    private String email;
    private String username;
    private int age;
    private String phone;
    private double height;
    private double weight;
}
