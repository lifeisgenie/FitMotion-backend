package backend.FitMotion.filter;

import backend.FitMotion.dto.CustomUserDetails;
import backend.FitMotion.entity.User;
import backend.FitMotion.jwt.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTUtil jwtUtil;

    @Autowired
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        // Authorization 헤더가 존재하고 "Bearer "로 시작하는지 확인
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // "Bearer " 이후의 JWT를 추출
            String token = authorization.substring(7);

            try {
                // 토큰 소멸 시간 검증
                if (jwtUtil.isTokenExpired(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token expired");
                    return;
                }

                String email = jwtUtil.extractEmail(token);
                setUpSpringAuthentication(email);

                // 필터 체인을 계속 진행
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                logger.error("Error setting authentication: ", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Internal server error");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void setUpSpringAuthentication(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("temppassword"); // Placeholder password

        // 스프링 시큐리티 인증 토큰 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 세션에 사용자 등록 -> 특정한 경로에 접근 가능
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
