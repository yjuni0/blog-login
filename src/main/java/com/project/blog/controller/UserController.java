package com.project.blog.controller;


import com.project.blog.common.exception.UserException;
import com.project.blog.dto.request.user.LoginDto;
import com.project.blog.dto.request.user.RegisterDto;
import com.project.blog.dto.response.user.UserResponseDto;
import com.project.blog.dto.response.user.UserTokenDto;
import com.project.blog.entity.User;
import com.project.blog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/checkId")
    public ResponseEntity<?> checkIdDuplicate(@RequestParam String email){
        log.info("이메일 중복 체크 요청");
        userService.isExistUserEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterDto registerDto){
        UserResponseDto registerUser = userService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser);
    }
    @PostMapping("/login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        log.info("로그인 요청");
        try {
            UserTokenDto loginUser = userService.login(loginDto, response);
            return ResponseEntity.status(HttpStatus.OK).header("Authorization","Bearer "+loginUser.getAccessToken()).body(loginUser);
        } catch (UserException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(null); // 또는 에러 메시지 포함 DTO
        }
    }

    @PostMapping("/checkPwd")
    public ResponseEntity<UserResponseDto> check(@AuthenticationPrincipal User user, @RequestBody Map<String, String> request){
        log.info("비밀번호 확인 요청");
        String password = request.get("password");
        UserResponseDto memberInfo = userService.check(user, password);
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }
}
