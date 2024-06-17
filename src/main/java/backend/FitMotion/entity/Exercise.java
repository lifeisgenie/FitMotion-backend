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
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long exerciseId;

    @Column(name = "exercise_name")
    private String exerciseName;

    @Column(name = "exercise_category")
    private String exerciseCategory;

    @Column(name = "exercise_explain")
    private String exerciseExplain;

    @Column(name = "exercise_url")
    private String exerciseUrl;
}
