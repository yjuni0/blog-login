package com.project.blog.common.jwt;


import com.project.blog.entity.RefreshToken;
import com.project.blog.entity.User;
import com.project.blog.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.CookieHandler;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService userDetailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String HEADER_STRING;

    @Value("${jwt.prefix}")
    private String TOKEN_PREFIX;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_STRING);
        String userEmail = null;
        String accessToken = null;

        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            accessToken = header.substring(TOKEN_PREFIX.length());
            try {
                userEmail = this.jwtUtil.getUsernameFromToken(accessToken);
                log.info("JWT 토큰에서 사용자명 추출 완료: {}", userEmail);
            } catch (ExpiredJwtException e) {
                log.info("액세스 토큰 만료, 리프레시 토큰 확인");
                userEmail = e.getClaims().getSubject();
                log.info("만료된 토큰에서 추출한 이메일: {}", userEmail);
                Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByEmail(userEmail);
                log.info(refreshTokenOpt.toString());
                if (refreshTokenOpt.isPresent() && this.jwtUtil.validateRefreshToken(refreshTokenOpt.get().getToken())) {
                    UserDetails userDetails = this.userDetailService.loadUserByUsername(userEmail);
                    String newAccessToken = this.jwtUtil.generationAccessToken((User) userDetails);
                    response.setHeader(HEADER_STRING, TOKEN_PREFIX + newAccessToken);
                    accessToken = newAccessToken;
                    log.info("새로운 액세스 토큰 발급 완료");
                } else {
                    log.error("유효한 리프레시 토큰이 없음");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("토큰이 만료되었습니다. 다시 로그인해주세요.");
                    return;
                }
            } catch (Exception e) {
                log.error("토큰 검증 중 오류 발생", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("토큰 검증 오류");
                return;
            }
        } else {
            log.warn("토큰이 헤더에 포함되어 있지 않거나 잘못된 형식");
        }


        // 사용자 이름이 존재하고, 현재 인증 정보가 없을 때
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 사용자 정보 로드
            UserDetails userDetails = this.userDetailService.loadUserByUsername(userEmail);

            // 토큰 유효성 검사
            if (this.jwtUtil.validateToken(accessToken, userDetails)) {
                // 새로운 인증 토큰 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 인증 세부 정보 설정
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                // 토큰 유효하지 않을 경우 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("유효하지 않은 토큰");
            }
        }
        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}