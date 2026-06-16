package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.AuthResponse;
import com.donel.taskmanager.dto.LoginRequest;
import com.donel.taskmanager.dto.RegisterRequest;
import com.donel.taskmanager.model.UserAccount;
import com.donel.taskmanager.repository.UserAccountRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserAccountRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(existing -> {
            throw new IllegalArgumentException("A user with this email already exists.");
        });

        UserAccount user = new UserAccount(
                request.displayName(),
                request.email(),
                passwordEncoder.encode(request.password())
        );
        UserAccount saved = userRepository.save(user);
        return new AuthResponse("Bearer", jwtService.generateToken(saved), UserService.toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.email()));
        return new AuthResponse("Bearer", jwtService.generateToken(user), UserService.toResponse(user));
    }
}
