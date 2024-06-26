package backend.FitMotion.controller;

import backend.FitMotion.dto.request.*;
import backend.FitMotion.dto.response.*;
import backend.FitMotion.exception.EmailAlreadyExistsException;
import backend.FitMotion.exception.UserNotFoundException;
import backend.FitMotion.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessageDTO> signUp(@RequestBody RequestSignUpDTO dto) {
        try {
            ResponseMessageDTO result = userService.signUp(dto);
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatusCode()));
        } catch (EmailAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원 가입 실패"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessageDTO> logout() {
        ResponseMessageDTO response = userService.logout();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    /**
     * 개인정보 조회
     */
    @GetMapping("/profile/detail/{Email}")
    public ResponseEntity<ResponseProfileDTO> profileDetail(@PathVariable("Email") String Email){
        ResponseProfileDTO response = userService.ResponseUserProfile(Email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * 개인정보 수정
     */
    @PutMapping("/profile/update")
    public ResponseEntity<ResponseMessageDTO> updateUserProfile(@RequestBody RequestUpdateDTO dto, Authentication authentication) {
        try {
            String email = authentication.getName(); // 사용자 인증 정보에서 이메일을 가져옴
            ResponseMessageDTO result = userService.updateUserProfile(email, dto); // result 변수를 선언 및 초기화
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatusCode()));
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(new ResponseMessageDTO(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다."), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new ResponseMessageDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "회원 정보 수정 실패"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 비밀번호 변경
     */
    @PostMapping("/profile/password")
    public ResponseEntity<ResponseMessageDTO> changePassword(@RequestBody RequestPasswordDTO dto) {
        try {
            userService.changePassword(dto);
            ResponseMessageDTO response = new ResponseMessageDTO(HttpStatus.OK.value(), "비밀번호가 성공적으로 변경되었습니다.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            ResponseMessageDTO response = new ResponseMessageDTO(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 운동 상세 조회
     */
    @GetMapping("/exercise/detail/{exerciseName}")
    public ResponseEntity<ResponseExerciseDetailDTO> getExerciseDetailByName(@PathVariable String exerciseName) {
        ResponseExerciseDetailDTO response = userService.getExerciseDetailByName(exerciseName);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * 운동 리스트 조회
     */
    @GetMapping("/exercise/list")
    public ResponseEntity<ResponseExerciseListsDTO> getAllExercises() {
        ResponseExerciseListsDTO response = userService.getAllExercises();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * 피드백 상세 조회
     */
    @GetMapping("/feedback/detail/{feedbackId}")
    public ResponseEntity<ResponseFeedbackDetailDTO> getFeedbackDetail(@PathVariable Long feedbackId) {
        try {
            ResponseFeedbackDetailDTO response = userService.getFeedbackDetail(feedbackId);
            if (response.getStatusCode() == 200) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFeedbackDetailDTO(500, "피드백 조회 실패", null));
        }
    }

    /**
     * 피드백 리스트 조회
     */
    @GetMapping("/feedback/list/{userId}")
    public ResponseEntity<ResponseFeedbackListDTO> getFeedbackList(@PathVariable("userId") Long userId) {
        try {
            List<ResponseFeedbackListDTO.FeedbackInfo> feedbackList = userService.getFeedbackListByUserId(userId);
            ResponseFeedbackListDTO.FeedbackData data = new ResponseFeedbackListDTO.FeedbackData(feedbackList);
            return ResponseEntity.ok(new ResponseFeedbackListDTO(200, "피드백 리스트 조회 성공", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFeedbackListDTO(500, "피드백 리스트 조회 실패", null));
        }
    }

    /**
     * 피드백 저장
     */
    @PostMapping("/feedback/save")
    public ResponseEntity<ResponseFeedbackDetailDTO> saveFeedback(@RequestBody RequestFeedbackSaveDTO request) {
        try {
            ResponseFeedbackDetailDTO response = userService.saveFeedback(request);
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFeedbackDetailDTO(500, "피드백 저장 실패", null));
        }
    }
}
