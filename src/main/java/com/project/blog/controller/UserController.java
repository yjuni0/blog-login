package com.project.blog.controller;


import com.project.blog.common.exception.UserException;
import com.project.blog.dto.request.LoginDto;
import com.project.blog.dto.request.RegisterDto;
import com.project.blog.dto.response.UserResponseDto;
import com.project.blog.dto.response.UserTokenDto;
import com.project.blog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody RegisterDto registerDto){
        UserResponseDto registerUser = userService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser);
    }
    @PostMapping("/login")
    public ResponseEntity<UserTokenDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        try {
            UserTokenDto loginUser = userService.login(loginDto, response);
            return ResponseEntity.ok(loginUser);
        } catch (UserException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(null); // 또는 에러 메시지 포함 DTO
        }
    }
}
