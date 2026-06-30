package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.AuthResponse;
import com.donel.taskmanager.dto.ForgotPasswordRequest;
import com.donel.taskmanager.dto.LoginRequest;
import com.donel.taskmanager.dto.RegisterRequest;
import com.donel.taskmanager.dto.ResetPasswordRequest;
import com.donel.taskmanager.model.PasswordResetToken;
import com.donel.taskmanager.model.UserAccount;
import com.donel.taskmanager.repository.PasswordResetTokenRepository;
import com.donel.taskmanager.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserAccountRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final int passwordResetExpirationMinutes;
    private final String passwordResetUrl;

    public AuthService(
            UserAccountRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            @Value("${app.security.password-reset.expiration-minutes:15}") int passwordResetExpirationMinutes,
            @Value("${app.frontend.password-reset-url:taskmanager://reset-password}") String passwordResetUrl
    ) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.passwordResetExpirationMinutes = passwordResetExpirationMinutes;
        this.passwordResetUrl = passwordResetUrl;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        userRepository.findByEmail(email).ifPresent(existing -> {
            throw new IllegalArgumentException("A user with this email already exists.");
        });

        UserAccount user = new UserAccount(
                request.displayName().trim(),
                email,
                passwordEncoder.encode(request.password())
        );
        UserAccount saved = userRepository.save(user);
        return new AuthResponse("Bearer", jwtService.generateToken(saved), UserService.toResponse(saved));
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizeEmail(request.email()), request.password())
        );

        String email = normalizeEmail(request.email());
        UserAccount user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return new AuthResponse("Bearer", jwtService.generateToken(user), UserService.toResponse(user));
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(normalizeEmail(request.email())).ifPresent(user -> {
            passwordResetTokenRepository.deleteByUser(user);

            String token = generateToken();
            PasswordResetToken passwordResetToken = new PasswordResetToken(
                    user,
                    hashToken(token),
                    Instant.now().plusSeconds(passwordResetExpirationMinutes * 60L)
            );
            passwordResetTokenRepository.save(passwordResetToken);

            emailService.sendPasswordReset(user, buildPasswordResetLink(token));
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenHashAndUsedFalse(hashToken(request.token().trim()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired password reset token."));

        if (passwordResetToken.isExpired()) {
            passwordResetToken.markUsed();
            throw new IllegalArgumentException("Invalid or expired password reset token.");
        }

        UserAccount user = passwordResetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        passwordResetToken.markUsed();
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String buildPasswordResetLink(String token) {
        String separator = passwordResetUrl.contains("?") ? "&" : "?";
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        return passwordResetUrl + separator + "token=" + encodedToken;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available.", exception);
        }
    }
}
