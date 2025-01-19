package com.project.blog.controller;


import com.project.blog.dto.request.RegisterDto;
import com.project.blog.dto.response.UserResponseDto;
import com.project.blog.service.UserService;
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
}
