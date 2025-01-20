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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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


    // 입력된 비밀번호와 디비 암호화된비밀번호 확인
    public void checkEncodePassword(String rawPassword, String encodedPassword){
        if(!passwordEncoder.matches(rawPassword,encodedPassword)){
            throw new UserException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }
    }


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

    // 저장된 이메일과 비밀번호 일치 확인
    private void authenticate(String email, String password) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
        }catch (DisabledException de){
            throw new UserException(HttpStatus.BAD_REQUEST,"인증되지 않은 아이디입니다.");
        }catch (BadCredentialsException bce){
            throw new UserException(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");
        }
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
    @Transactional
    public UserTokenDto login(LoginDto loginDto, HttpServletResponse response) {
        try {
            // 기존 인증 로직
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UserException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));

            // 비밀번호 검증
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                throw new UserException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
            }

            // 토큰 생성
            UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDto.getEmail());
            String accessToken = jwtUtil.generationAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 쿠키 생성 및 설정
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            response.addCookie(accessTokenCookie);

            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            response.addCookie(refreshTokenCookie);

            // 리프레시 토큰 저장
            RefreshToken dbRefreshToken = RefreshToken.builder()
                    .email(loginDto.getEmail())
                    .token(refreshToken)
                    .build();
            refreshTokenRepository.save(dbRefreshToken);

            // 토큰 정보 포함 DTO 반환
            return UserTokenDto.fromEntity(userDetails, accessToken, refreshToken);
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
