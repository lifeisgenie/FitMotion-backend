package backend.FitMotion.service;

import backend.FitMotion.dto.request.RequestSignUpDTO;
import backend.FitMotion.dto.request.RequestUpdateDTO;
import backend.FitMotion.dto.response.ResponseMessageDTO;
import backend.FitMotion.dto.response.ResponseProfileDTO;
import backend.FitMotion.entity.User;
import backend.FitMotion.entity.UserProfile;
import backend.FitMotion.exception.EmailAlreadyExistsException;
import backend.FitMotion.exception.UserNotFoundException;
import backend.FitMotion.repository.UserProfileRepository;
import backend.FitMotion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
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
    public ResponseProfileDTO getUserProfile(String email) {
        UserProfile userProfile = userProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseProfileDTO.builder()
                .userId(userProfile.getUserId())
                .email(userProfile.getUser().getEmail())
                .username(userProfile.getUsername())
                .age(userProfile.getAge())
                .phone(userProfile.getPhone())
                .height(userProfile.getHeight())
                .weight(userProfile.getWeight())
                .build();
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
}
