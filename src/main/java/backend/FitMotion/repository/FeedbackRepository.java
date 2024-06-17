package backend.FitMotion.repository;

import backend.FitMotion.entity.FeedbackFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackFile, Long> {
    @Query("SELECT f FROM FeedbackFile f WHERE f.userProfile.userId = :userId")
    List<FeedbackFile> findByUserId(@Param("userId") Long userId);
}
