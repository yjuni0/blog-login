//package com.project.blog.common.jwt;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class TokenController {
//
//    private final JwtUtil jwtUtil;
//
//    @GetMapping("/api/token")
//    public ResponseEntity<String> checkTokenValid(String accessToken) {
//
//        try{
//            String userToken = "Bearer " + accessToken;
//
//            if(!jwtUtil.isTokenExpired(userToken)){
//                String newAccessToken =
//                return ResponseEntity.status(HttpStatus.OK).body(userToken);
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//}
