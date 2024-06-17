package backend.FitMotion.dto.request;

import lombok.Data;

@Data
public class RequestUpdateDTO {
    private String username;
    private int age;
    private String phone;
    private double height;
    private double weight;
}
