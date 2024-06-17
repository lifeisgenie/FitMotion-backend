package backend.FitMotion.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSignUpDTO {
    private String email;
    private String password;
    private String username;
    private int age;
    private String phone;
    private double height;
    private double weight;
}
