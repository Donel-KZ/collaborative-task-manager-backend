package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.UserResponse;
import com.donel.taskmanager.model.UserAccount;
import com.donel.taskmanager.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserAccountRepository userRepository;

    public UserService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserService::toResponse)
                .toList();
    }

    public UserAccount requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public static UserResponse toResponse(UserAccount user) {
        return new UserResponse(user.getId(), user.getDisplayName(), user.getEmail());
    }
}
