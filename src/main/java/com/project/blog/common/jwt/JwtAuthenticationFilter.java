package com.project.blog.common.jwt;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailService userDetailService;
    private final JwtUtil jwtUtil;

    @Value("${jwt.header}")
    private String HEADER_STRING;

    @Value("${jwt.refresh.header}")
    private String REFRESH_HEADER_STRING;

    @Value("${jwt.prefix}")
    private String TOKEN_PREFIX;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_STRING);
        String refreshHeader = request.getHeader(REFRESH_HEADER_STRING);
        String username = null;
        String authToken = null;
        String refreshToken = null;

        if(header != null && header.startsWith(TOKEN_PREFIX)){
            authToken = header.substring(TOKEN_PREFIX.length());
            try {
                username = this.jwtUtil.getUsernameFromToken(authToken);
                log.info("JWT 토큰에서 사용자명 추출 완료");
            } catch (ExpiredJwtException eje) {
                log.info("액세스 토큰 만료, 리프레시 토큰 확인");
                if (refreshHeader != null && refreshHeader.startsWith(TOKEN_PREFIX)) {
                    refreshToken = refreshHeader.substring(TOKEN_PREFIX.length());
                    try {
                        username = this.jwtUtil.getUsernameFromToken(refreshToken);
                        if (this.jwtUtil.validateRefreshToken(refreshToken)) {
                            String newAccessToken = this.jwtUtil.generationAccessToken(this.userDetailService.loadUserByUsername(username));
                            response.setHeader(HEADER_STRING, TOKEN_PREFIX + newAccessToken);
                            authToken = newAccessToken;
                            log.info("새로운 액세스 토큰 발급 완료");
                        } else {
                            log.error("리프레시 토큰 만료");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
                            return;
                        }
                    } catch (Exception e) {
                        log.error("리프레시 토큰 검증 중 오류 발생", e);
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        response.getWriter().write("리프레시 토큰 검증 오류");
                        return;
                    }
                } else {
                    log.error("리프레시 토큰이 없음");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("리프레시 토큰이 없습니다. 다시 로그인해주세요.");
                    return;
                }
            } catch (Exception e) {
                log.error("토큰 검증 중 오류 발생", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("토큰 검증 오류");
                return;
            }
        } else {
            log.warn("토큰이 헤더에 포함되어있지 않거나 잘못된 형식");
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailService.loadUserByUsername(username);
            if (this.jwtUtil.validateToken(authToken, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                log.info("유효하지 않은 토큰");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("유효하지 않은 토큰");
                return;
            }
        } else {
            log.info("사용자 이름이 null 이거나 인증 정보가 이미 설정되어 있습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
