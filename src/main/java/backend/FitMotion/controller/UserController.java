package backend.FitMotion.controller;

import backend.FitMotion.dto.request.RequestSignUpDTO;
import backend.FitMotion.dto.response.ResponseMessageDTO;
import backend.FitMotion.exception.EmailAlreadyExistsException;
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
}
