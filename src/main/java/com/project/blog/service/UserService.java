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
        isExistUserEmail(registerDto.getEmail());
        verifyPasswordMatch(registerDto.getPassword(),registerDto.getCheckPassword());
        User saveUser = userRepository.save(RegisterDto.ofEntity(registerDto));
        return UserResponseDto.fromEntity(saveUser);
    }

    // 로그인 메서드
    public UserTokenDto login(LoginDto loginDto, HttpServletResponse response){
        try{
            // 사용자 인증
            authenticate(loginDto.getEmail(), loginDto.getPassword());
            // 사용자 정보 로드
            UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDto.getEmail());
            // 비밀번호 확인
            checkEncodePassword(loginDto.getPassword(),userDetails.getPassword());

            // User 엔티티 조회
            User user = userRepository.findByEmail(loginDto.getEmail())
                    .orElseThrow(() -> new UserException(HttpStatus.BAD_REQUEST, "사용자를 찾을 수 없습니다."));
            // 토큰 생성
            String accessToken=jwtUtil.generationAccessToken(userDetails);
            String userRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            // 리프레시 토큰 디비에 저장
            RefreshToken dbRefreshToken = RefreshToken.builder()
                    .email(loginDto.getEmail())
                    .token(userRefreshToken)
                    .build();
            refreshTokenRepository.save(dbRefreshToken);

            response.setHeader("Authorization", "Bearer " + accessToken);
            response.setHeader("Authorization-refresh", "Bearer " + userRefreshToken);

            //로그인 유저용 Token 포함 Dto 반환
            return UserTokenDto.fromEntity(userDetails,accessToken,userRefreshToken);
        }catch (Exception e){
            log.info("로그인 처리중 오류 발생", e);
            throw e;
        }
    }
    // 유저 정보 수정 메서드
    public UserResponseDto update(Long id,UserUpdateDto updateDto){
        User updateUser = userRepository.findById(id).orElseThrow(()->new UserException(HttpStatus.BAD_REQUEST,"업데이트 중 오류 발생"));
        return UserResponseDto.fromEntity(updateUser);
    }

}
