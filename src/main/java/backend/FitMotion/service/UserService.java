package backend.FitMotion.service;

import backend.FitMotion.dto.request.RequestFeedbackSaveDTO;
import backend.FitMotion.dto.request.RequestPasswordDTO;
import backend.FitMotion.dto.request.RequestSignUpDTO;
import backend.FitMotion.dto.request.RequestUpdateDTO;
import backend.FitMotion.dto.response.*;
import backend.FitMotion.entity.Exercise;
import backend.FitMotion.entity.FeedbackFile;
import backend.FitMotion.entity.User;
import backend.FitMotion.entity.UserProfile;
import backend.FitMotion.exception.EmailAlreadyExistsException;
import backend.FitMotion.exception.ExerciseNotFoundException;
import backend.FitMotion.exception.UserNotFoundException;
import backend.FitMotion.repository.ExerciseRepository;
import backend.FitMotion.repository.FeedbackRepository;
import backend.FitMotion.repository.UserProfileRepository;
import backend.FitMotion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ExerciseRepository exerciseRepository;
    private final FeedbackRepository feedbackRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public ResponseMessageDTO signUp(RequestSignUpDTO dto) {
        String email = dto.getEmail();
        Boolean isExist = userRepository.existsByEmail(email);

        if (isExist) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        try {
            User user = User.builder()
                    .email(dto.getEmail())
                    .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                    .build();
            userRepository.save(user);

            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .username(dto.getUsername())
                    .age(dto.getAge())
                    .phone(dto.getPhone())
                    .height(dto.getHeight())
                    .weight(dto.getWeight())
                    .build();

            userProfileRepository.save(userProfile);

            return new ResponseMessageDTO(HttpStatus.CREATED.value(), "회원 가입 성공");
        } catch (Exception e) {
            return new ResponseMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원 가입 실패");
        }
    }

    /**
     * 로그아웃
     */
    public ResponseMessageDTO logout() {
        try {
            return new ResponseMessageDTO(HttpStatus.OK.value(), "로그아웃 성공");
        } catch (Exception e) {
            return new ResponseMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "로그아웃 실패");
        }
    }

    /**
     * 개인정보 조회
     */
    @Transactional
    public ResponseProfileDTO ResponseUserProfile(String email) {
        try {
            Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserEmail(email);
            if(userProfileOptional.isPresent()) {
                UserProfile _userprofile = userProfileOptional.get();
                ResponseProfileDTO.userProfile uf = new ResponseProfileDTO.userProfile (
                        _userprofile.getUserId(),
                        _userprofile.getUsername(),
                        _userprofile.getAge(),
                        _userprofile.getPhone(),
                        _userprofile.getHeight(),
                        _userprofile.getWeight()
                );
                return new ResponseProfileDTO(200, "유저 조회 성공" ,uf);
            } else {
                return new ResponseProfileDTO(500, "유저 조회 실패", null);
            }
        } catch (Exception e) {
            return new ResponseProfileDTO(500, "유저 조회 실패", null);
        }
    }

    /**
     * 개인정보 수정
     */
    @Transactional
    public ResponseMessageDTO updateUserProfile(String email, RequestUpdateDTO dto) {
        try {
            UserProfile userProfile = userProfileRepository.findByUserEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            userProfile.setUsername(dto.getUsername());
            userProfile.setAge(dto.getAge());
            userProfile.setPhone(dto.getPhone());
            userProfile.setHeight(dto.getHeight());
            userProfile.setWeight(dto.getWeight());

            userProfileRepository.save(userProfile);

            return new ResponseMessageDTO(HttpStatus.OK.value(), "개인정보 수정 성공");
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("개인정보 수정 실패", e);
        }
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(RequestPasswordDTO dto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            throw new IllegalArgumentException("인증된 사용자가 아닙니다.");
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
        }

        User user = optionalUser.get();

        if (!bCryptPasswordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        if (dto.getNewPassword().length() < 8 ||
                !dto.getNewPassword().matches(".*[A-Z].*") ||
                !dto.getNewPassword().matches(".*[a-z].*") ||
                !dto.getNewPassword().matches(".*\\d.*") ||
                !dto.getNewPassword().matches(".*[@#$%^&+=!].*")) {
            throw new IllegalArgumentException("새 비밀번호가 비밀번호 정책을 만족하지 않습니다.");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 확인 비밀번호와 일치하지 않습니다.");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(dto.getNewPassword());

        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    /**
     * 운동 상세 조회
     */
    public ResponseExerciseDetailDTO getExerciseDetailByName(String exerciseName) {
        try {
            Optional<Exercise> exerciseOptional = exerciseRepository.findByExerciseName(exerciseName);
            if (exerciseOptional.isPresent()) {
                Exercise exercise = exerciseOptional.get();
                ResponseExerciseDetailDTO.ExerciseData exerciseData = new ResponseExerciseDetailDTO.ExerciseData(
                        exercise.getExerciseName(),
                        exercise.getExerciseCategory(),
                        exercise.getExerciseExplain(),
                        exercise.getExerciseUrl()
                );
                return new ResponseExerciseDetailDTO(200, "운동 상세 조회 성공", exerciseData);
            } else {
                throw new ExerciseNotFoundException("운동을 찾을 수 없습니다");
            }
        } catch (ExerciseNotFoundException e) {
            return new ResponseExerciseDetailDTO(404, e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseExerciseDetailDTO(500, "운동 조회 중 오류 발생", null);
        }
    }

    /**
     * 운동 상세 조회 (ID로)
     */
    public ResponseExerciseDetailDTO getExerciseDetailById(Long exerciseId) {
        try {
            Optional<Exercise> exerciseOptional = exerciseRepository.findById(exerciseId);
            if (exerciseOptional.isPresent()) {
                Exercise exercise = exerciseOptional.get();
                ResponseExerciseDetailDTO.ExerciseData exerciseData = new ResponseExerciseDetailDTO.ExerciseData(
                        exercise.getExerciseName(),
                        exercise.getExerciseCategory(),
                        exercise.getExerciseExplain(),
                        exercise.getExerciseUrl()
                );
                return new ResponseExerciseDetailDTO(200, "운동 상세 조회 성공", exerciseData);
            } else {
                throw new ExerciseNotFoundException("운동을 찾을 수 없습니다");
            }
        } catch (ExerciseNotFoundException e) {
            return new ResponseExerciseDetailDTO(404, e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseExerciseDetailDTO(500, "운동 조회 중 오류 발생", null);
        }
    }

    /**
     * 운동 리스트 조회
     */
    public ResponseExerciseListsDTO getAllExercises() {
        try {
            List<Exercise> exercises = exerciseRepository.findAll();
            List<ResponseExerciseListsDTO.ExerciseInfo> exerciseList = exercises.stream()
                    .map(exercise -> new ResponseExerciseListsDTO.ExerciseInfo(
                            exercise.getExerciseName(),
                            exercise.getExerciseCategory(),
                            exercise.getExerciseExplain(),
                            exercise.getExerciseUrl()))
                    .collect(Collectors.toList());

            ResponseExerciseListsDTO.ExerciseData exerciseData = new ResponseExerciseListsDTO.ExerciseData(exerciseList);

            return new ResponseExerciseListsDTO(200, "운동 리스트 조회 성공", exerciseData);
        } catch (Exception e) {
            return new ResponseExerciseListsDTO(500, "운동 리스트 조회 실패", null);
        }
    }

    /**
     * 피드백 상세 조회
     */
    public ResponseFeedbackDetailDTO getFeedbackDetail(Long feedbackId) {
        Optional<FeedbackFile> feedbackOptional = feedbackRepository.findById(feedbackId);
        if (feedbackOptional.isPresent()) {
            FeedbackFile feedback = feedbackOptional.get();
            ResponseFeedbackDetailDTO.FeedbackData feedbackData = new ResponseFeedbackDetailDTO.FeedbackData(
                    feedback.getFeedbackId(),
                    feedback.getContent(),
                    feedback.getVideoUrl(),
                    feedback.getExercise().getExerciseId(),
                    feedback.getUserProfile().getUserId(),
                    feedback.getCreatedDate()
            );
            return new ResponseFeedbackDetailDTO(200, "피드백 조회 성공", feedbackData);
        } else {
            return new ResponseFeedbackDetailDTO(500, "피드백 조회 실패", null);
        }
    }

    /**
     * 피드백 리스트 조회
     */
    public List<ResponseFeedbackListDTO.FeedbackInfo> getFeedbackListByUserId(Long userId) {
        List<FeedbackFile> feedbackFiles = feedbackRepository.findByUserId(userId);
        return feedbackFiles.stream()
                .map(feedback -> {
                    ResponseExerciseDetailDTO exerciseDetail = getExerciseDetailById(feedback.getExercise().getExerciseId());
                    return new ResponseFeedbackListDTO.FeedbackInfo(
                            feedback.getFeedbackId(),
                            exerciseDetail,
                            feedback.getExercise().getExerciseId(),
                            feedback.getCreatedDate()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 피드백 저장
     */
    @Transactional
    public ResponseFeedbackDetailDTO saveFeedback(RequestFeedbackSaveDTO request) {
        UserProfile userProfile = userProfileRepository.findById(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new ExerciseNotFoundException("Exercise not found"));

        FeedbackFile feedbackFile = FeedbackFile.builder()
                .content(request.getContent())
                .videoUrl(request.getVideoUrl())
                .userProfile(userProfile)
                .exercise(exercise)
                .createdDate(new Date())
                .build();

        feedbackRepository.save(feedbackFile);

        ResponseFeedbackDetailDTO.FeedbackData feedbackData = new ResponseFeedbackDetailDTO.FeedbackData(
                feedbackFile.getFeedbackId(),
                feedbackFile.getContent(),
                feedbackFile.getVideoUrl(),
                feedbackFile.getExercise().getExerciseId(),
                feedbackFile.getUserProfile().getUserId(),
                feedbackFile.getCreatedDate()
        );

        return new ResponseFeedbackDetailDTO(200, "피드백 저장 성공", feedbackData);
    }
}
