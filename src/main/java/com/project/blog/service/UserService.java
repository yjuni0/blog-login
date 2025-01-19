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
import com.project.blog.entity.User;
import com.project.blog.repository.UserRepository;
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
    private final BCryptPasswordEncoder passwordEncoder; // password 암호화
    private final AuthenticationManager authenticationManager; // 인증 매니저
    private final CustomUserDetailService customUserDetailService; // loadUserByUsername
    private final JwtUtil jwtUtil; //토큰



    public void checkEncodePassword(String rawPassword, String encodedPassword){
        if(!passwordEncoder.matches(rawPassword,encodedPassword)){
            throw new UserException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.");
        }
    }

    public void checkPassword(String password, String checkPassword){
        if(password.equals(checkPassword)){
            throw new UserException(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");
        }
    }
    private void isExistUserEmail(String email) {
        if(userRepository.findByEmail(email).isPresent()){
            throw new UserException(HttpStatus.BAD_REQUEST,"이미 사용중인 이메일입니다.");
        }
    }


    private void authenticate(String email, String password) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));
        }catch (DisabledException de){
            throw new UserException(HttpStatus.BAD_REQUEST,"인증되지 않은 아이디입니다.");
        }catch (BadCredentialsException bce){
            throw new UserException(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다.");
        }
    }
    public HttpStatus checkIdDuplicate(String email){
        isExistUserEmail(email);
        return HttpStatus.OK;
    }

    public UserResponseDto check(User user, String password){
        User checkUser = (User) customUserDetailService.loadUserByUsername(user.getEmail());
        checkEncodePassword(password,checkUser.getPassword());
        return UserResponseDto.fromEntity(checkUser);
    }

    public UserResponseDto register(RegisterDto registerDto){
        isExistUserEmail(registerDto.getEmail());
        checkPassword(registerDto.getPassword(),registerDto.getCheckPassword());
        User saveUser = userRepository.save(RegisterDto.ofEntity(registerDto));
        return UserResponseDto.fromEntity(saveUser);
    }

    public UserTokenDto login(LoginDto loginDto){
        try{
            // 사용자 인증
            authenticate(loginDto.getEmail(), loginDto.getPassword());
            // 사용자 정보 로드
            UserDetails userDetails = customUserDetailService.loadUserByUsername(loginDto.getEmail());
            // 비밀번호 확인
            checkEncodePassword(loginDto.getPassword(),userDetails.getPassword());

            // 토큰 생성
            String token=jwtUtil.generationToken(userDetails);

            //UserTokenDto 반환
            return UserTokenDto.fromEntity(userDetails,token);
        }catch (Exception e){
            log.info("로그인 처리중 오류 발생", e);
            throw e;
        }
    }

    public UserResponseDto update(Long id,UserUpdateDto updateDto){
        User updateUser = userRepository.findById(id).orElseThrow(()->new UserException(HttpStatus.BAD_REQUEST,"업데이트 중 오류 발생"));
        return UserResponseDto.fromEntity(updateUser);
    }

}
