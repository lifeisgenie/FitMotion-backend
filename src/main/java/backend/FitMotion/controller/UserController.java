package backend.FitMotion.controller;

import backend.FitMotion.dto.request.RequestSignUpDTO;
import backend.FitMotion.dto.response.ResponseMessageDTO;
import backend.FitMotion.dto.response.ResponseProfileDTO;
import backend.FitMotion.exception.EmailAlreadyExistsException;
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
     * 개인정보 조회
     */
    @GetMapping("/profile/info")
    public ResponseEntity<ResponseProfileDTO> getUserProfile(Authentication authentication) {
        String email = authentication.getName(); // 사용자 인증 정보에서 이메일을 가져옴
        ResponseProfileDTO dto = userService.getUserProfile(email);
        return ResponseEntity.ok(dto);
    }
}
