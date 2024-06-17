package backend.FitMotion.repository;

import backend.FitMotion.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Optional<Exercise> findById(Long exerciseId);
    Optional<Exercise> findByExerciseName(String exerciseName);
}
