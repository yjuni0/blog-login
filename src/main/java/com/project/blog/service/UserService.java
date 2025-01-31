package com.project.blog.service;


import com.project.blog.common.exception.UserException;
import com.project.blog.common.jwt.CustomUserDetailService;
import com.project.blog.common.jwt.JwtUtil;
import com.project.blog.dto.request.user.LoginDto;
import com.project.blog.dto.request.user.RegisterDto;
import com.project.blog.dto.request.user.UserUpdateDto;
import com.project.blog.dto.response.user.UserDto;
import com.project.blog.dto.response.user.UserResponseDto;
import com.project.blog.dto.response.user.UserTokenDto;
import com.project.blog.entity.RefreshToken;
import com.project.blog.entity.User;
import com.project.blog.repository.RefreshTokenRepository;
import com.project.blog.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    // 비밀번호 확인 로직
    public UserResponseDto check(User user, String password){
        User checkMember =(User) customUserDetailService.loadUserByUsername(user.getEmail());
        checkEncodePassword(password,checkMember.getPassword());

        return UserResponseDto.fromEntity(checkMember);
    }

    // 사용자 입력한 비밀번호, DB에 저장된 비밀번호 같은지 체크 :인코딩 확인
    private void checkEncodePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
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
            // 토큰 생성 이전 리프레시 토큰이 디비에 있으면 삭제 후 진행
            refreshTokenRepository.findByEmail(user.getEmail()).ifPresent(refreshTokenRepository::delete);

            String accessToken = jwtUtil.generationAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            // 리프레시 토큰 저장 (엔티티 생성 시 만료 시간 자동 설정)
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .email(user.getEmail())
                    .token(refreshToken)
                    .createdDate(LocalDateTime.now())
                    .expiresDate(LocalDateTime.now().plusDays(7))
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);

            response.setHeader("Authorization", "Bearer " + accessToken);
            return UserTokenDto.fromEntity(user, accessToken);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }
    // 유저 정보 수정 메서드
    public UserResponseDto update(Long id,UserUpdateDto updateDto){
        User updateUser = userRepository.findById(id).orElseThrow(()->new UserException(HttpStatus.BAD_REQUEST,"업데이트 중 오류 발생"));
        return UserResponseDto.fromEntity(updateUser);
    }

    public UserDto getDetail(Long userId) {
        User getUserDetail = userRepository.findById(userId).orElseThrow(()->new UserException(HttpStatus.BAD_REQUEST,"해당 유저를 찾지 못함"));
        return UserDto.fromEntity(getUserDetail);
    }
}
