package com.project.blog.common.jwt;

import com.project.blog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j // 로깅
public class JwtUtil implements Serializable {

    //시리얼 넘버 자동 생성
    @Serial
    private static final long serialVersionUID = 2063975554678653566L;

    @Value("${jwt.tokenExpirationTime}")
    private Integer tokenExpirationTime;

    @Value("${jwt.refreshTokenExpirationTime}")
    private Integer refreshTokenExpirationTime;

    @Value("${jwt.secret}")
    private String secret;

    //토큰으로 username 확인
    public String getUsernameFromToken(String token) {
        log.info("토큰에 포함된 username 확인 ");
        return getClaimFromToken(token, Claims::getSubject);
    }

    // 토큰 인증 만료 날짜
    public Date getExpirationDateFromToken(String token) {
        log.info("토큰 만료 날짜 확인");
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //claimsResolver
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 토큰 비밀키 사용해서 토큰 정보 추출
    private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            log.error("만료된 토큰",e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("잘못된 토큰 서명", e);
            throw new RuntimeException(e);
        }
    }

    //토큰 만료 여부
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //JWT 사용자 response 용 토큰 생성
    public String generationAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getUserNickName());
        claims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        log.info("사용자용 토큰 생성 완료");
        return doGenerateAccessToken(claims, user.getUsername());
    }

    // 토큰 생성
    private String doGenerateAccessToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("my-blog")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationTime*30))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 토큰 검증
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
        log.info("리프레시 토큰 생성 완료");
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }

    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("my-blog")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime * 60 * 24 * 14))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 리프레시 토큰 검증
    public Boolean validateRefreshToken(String token) {
        return !isTokenExpired(token);
    }
}
