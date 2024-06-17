package backend.FitMotion.controller;

import backend.FitMotion.dto.request.RequestPasswordDTO;
import backend.FitMotion.dto.request.RequestSignUpDTO;
import backend.FitMotion.dto.request.RequestUpdateDTO;
import backend.FitMotion.dto.response.ResponseExerciseDetailDTO;
import backend.FitMotion.dto.response.ResponseExerciseListsDTO;
import backend.FitMotion.dto.response.ResponseMessageDTO;
import backend.FitMotion.dto.response.ResponseProfileDTO;
import backend.FitMotion.exception.EmailAlreadyExistsException;
import backend.FitMotion.exception.UserNotFoundException;
import backend.FitMotion.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ResponseExerciseDetailDTO> getExerciseDetail(@PathVariable String exerciseName) {
        ResponseExerciseDetailDTO response = userService.getExerciseDetail(exerciseName);
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
}
