package backend.FitMotion.filter;

import backend.FitMotion.dto.CustomUserDetails;
import backend.FitMotion.dto.request.RequestLoginDTO;
import backend.FitMotion.jwt.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// 로그인 검증을 위한 커스텀 UsernamePasswordAuthentication 필터
// HTTP 요청으로부터 인증 정보를 추출하고 이를 기반으로 인증 시도
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    // 회원 검증의 경우 UsernamePasswordAuthenticationFilter가 호출한 AuthenticationManager를 통해 진행하며
    // DB에서 조회한 데이터를 UserDetailsService를 통해 받음
    private final AuthenticationManager authenticationManager;

    // 로그인이 성공됐을 때 실행되는 메소드에서 JWT를 발급받아 응답
    private final JWTUtil jwtUtil;

    private final Long accessTokenExpiration;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, Long accessTokenExpiration) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    // HTTP 요청으로부터 인증 정보를 추출하고, 이를 기반으로 인증 시도.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            RequestLoginDTO dto = objectMapper.readValue(messageBody, RequestLoginDTO.class);

            String email = dto.getEmail();
            String password = dto.getPassword();

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String email = customUserDetails.getUsername();
        String accessToken = jwtUtil.createJwt(email, accessTokenExpiration);

        // header는 ("키 값", "접두사+띄어쓰기" + token) 이렇게 응답
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + accessToken + "\"}");
    }

    // 로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"Authentication failed\"}");
    }
}
