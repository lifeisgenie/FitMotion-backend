package backend.FitMotion.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false)
    private User user;

    private String username;
    private int age;
    private String phone;
    private double height;
    private double weight;

    @OneToMany(mappedBy = "userProfile")
    private List<FeedbackFile> feedbackFiles;
}
