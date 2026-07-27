package com.example.auth;

import java.time.Instant;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.repository.UserRepository;
import com.example.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
            .orElseThrow(() ->
                new BadCredentialsException("Email hoặc mật khẩu không đúng")
            );

        if (!passwordEncoder.matches(
            request.password(),
            user.getPasswordHash()
        )) {
            throw new BadCredentialsException(
                "Email hoặc mật khẩu không đúng"
            );
        }

        userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(user.getEmail());
        Instant expiresAt = jwtService.getExpiresAt(token);

        return new AuthResponse(
            "Bearer",
            token,
            expiresAt
        );
    }
}
