package backend.FitMotion.dto.request;

import lombok.Data;

@Data
public class RequestPasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
