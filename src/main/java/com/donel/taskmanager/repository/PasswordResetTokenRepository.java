package com.donel.taskmanager.repository;

import com.donel.taskmanager.model.PasswordResetToken;
import com.donel.taskmanager.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);

    void deleteByUser(UserAccount user);
}
