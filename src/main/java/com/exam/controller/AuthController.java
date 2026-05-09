package com.exam.controller;

import com.exam.dto.AuthRequest;
import com.exam.dto.AuthResponse;
import com.exam.entity.User;
import com.exam.repository.UserRepository;
import com.exam.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            String token = jwtUtils.generateToken(request.getUsername());
            User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
            return ResponseEntity.ok(new AuthResponse(
                token, user.getId(), user.getUsername(), user.getFullName(), user.getRole().name()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Sai tên đăng nhập hoặc mật khẩu"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse(
            null, user.getId(), user.getUsername(), user.getFullName(), user.getRole().name()));
    }
}
