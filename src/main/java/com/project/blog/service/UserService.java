package com.project.blog.service;


import com.project.blog.common.PwdEncoderConfig;
import com.project.blog.common.exception.UserException;
import com.project.blog.common.jwt.CustomUserDetailService;
import com.project.blog.common.jwt.JwtUtil;
import com.project.blog.dto.request.LoginDto;
import com.project.blog.dto.request.RegisterDto;
import com.project.blog.dto.request.UserUpdateDto;
import com.project.blog.dto.response.UserResponseDto;
import com.project.blog.dto.response.UserTokenDto;
import com.project.blog.entity.RefreshToken;
import com.project.blog.entity.User;
import com.project.blog.repository.RefreshTokenRepository;
import com.project.blog.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository; //user Repo
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder passwordEncoder; // password 암호화
    private final AuthenticationManager authenticationManager; // 인증 매니저
    private final CustomUserDetailService customUserDetailService; // loadUserByUsername
    private final JwtUtil jwtUtil; //토큰


    // 비밀번호와 확인 비밀번호 일치 확인
    public void verifyPasswordMatch(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new UserException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }
    }

    public boolean isExistUserEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new UserException(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다.");
        }
        return false; // 이메일이 존재하지 않으면 false 반환
    }


    // 회원가입 메서드
    public UserResponseDto register(RegisterDto registerDto){
        // 이메일 중복 체크
        isExistUserEmail(registerDto.getEmail());

        // 비밀번호 일치 확인
        verifyPasswordMatch(registerDto.getPassword(), registerDto.getCheckPassword());

        // 비밀번호 암호화
        registerDto.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // 암호화된 비밀번호로 엔티티 생성 및 저장
        User saveUser = userRepository.save(RegisterDto.ofEntity(registerDto));

        return UserResponseDto.fromEntity(saveUser);
    }

    // 로그인 메서드
    public UserTokenDto login(LoginDto loginDto, HttpServletResponse response) {
        try {
            // 사용자 엔티티 조회
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UserException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

            // 비밀번호 검증
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new UserException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
            }

            // 기존 리프레시 토큰 조회
            Optional<RefreshToken> existingRefreshToken =
                    refreshTokenRepository.findByEmail(loginDto.getEmail());

            String userRefreshToken;
            if (existingRefreshToken.isPresent()) {
                // 기존 토큰 재사용
                userRefreshToken = existingRefreshToken.get().getToken();
            } else {
                // 새 토큰 생성
                UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDto.getEmail());
                userRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                RefreshToken newRefreshToken = RefreshToken.builder()
                        .email(loginDto.getEmail())
                        .token(userRefreshToken)
                        .build();
                refreshTokenRepository.save(newRefreshToken);
            }

            // 나머지 로직은 동일
            UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDto.getEmail());
            String accessToken = jwtUtil.generationAccessToken(userDetails);

            Cookie refreshTokenCookie = new Cookie("refreshToken", userRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Authorization-refresh", "Bearer " + userRefreshToken);

            return UserTokenDto.fromEntity(userDetails, accessToken);
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            throw e;
        }
    }
    // 유저 정보 수정 메서드
    public UserResponseDto update(Long id,UserUpdateDto updateDto){
        User updateUser = userRepository.findById(id).orElseThrow(()->new UserException(HttpStatus.BAD_REQUEST,"업데이트 중 오류 발생"));
        return UserResponseDto.fromEntity(updateUser);
    }

}
