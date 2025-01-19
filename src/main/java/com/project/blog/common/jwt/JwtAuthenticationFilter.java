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

    @Value("${jwt.prefix}")
    private String TOKEN_PREFIX;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authToken = null;
        // 헤더가 null 이 아니고 Bearer 로 시작하는지 확인
        if(header != null && header.startsWith(TOKEN_PREFIX)){
            authToken=header.substring(TOKEN_PREFIX.length()); // 토큰의 "Bearer " 이후
            try{
                username=this.jwtUtil.getUsernameFromToken(authToken);
                log.info("JWT 토큰에서 사용자명 추출 완료");
            }catch (ExpiredJwtException eje){
                log.error("토큰 만료", eje);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("만료된 토큰입니다.");
                return;
            }catch (Exception e){
                log.error("토큰 검증 중 오류 발생", e);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("토큰 검증 오류");
                return;
            }
        }else {
            log.warn("토큰이 헤더에 포함되어있지 않거나 잘못된 형식");
        }
        // username 이 존재하고 컨텍스트에 인증 정보가 없는 경우
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailService.loadUserByUsername(username);
            //토큰이 유효한지 검증
            if (this.jwtUtil.validateToken(authToken,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 스프링시큐리티 컨텍스트에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                log.info("유효하지 않은 토큰");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("유효하지 않은 토큰");
                return;
            }
        }else {
            log.info("사용자 이름이 null 이거나 인증 정보가 이미 설정되어 있습니다.");
        }
        // 필터 체인을 통해 요청 전달
        filterChain.doFilter(request,response);
    }
}
