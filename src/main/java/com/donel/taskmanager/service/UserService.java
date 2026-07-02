package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.UserResponse;
import com.donel.taskmanager.model.UserAccount;
import com.donel.taskmanager.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class UserService {

    private final UserAccountRepository userRepository;
    private final ProfilePictureStorageService profilePictureStorageService;

    public UserService(UserAccountRepository userRepository, ProfilePictureStorageService profilePictureStorageService) {
        this.userRepository = userRepository;
        this.profilePictureStorageService = profilePictureStorageService;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserService::toResponse)
                .toList();
    }

    public UserResponse findCurrentUser(String email) {
        return toResponse(requireUserByEmail(email));
    }

    @Transactional
    public UserResponse updateProfilePicture(String email, MultipartFile file) {
        UserAccount user = requireUserByEmail(email);
        String previousFilename = user.getProfilePictureFilename();
        String filename = profilePictureStorageService.store(file);

        user.setProfilePictureFilename(filename);
        profilePictureStorageService.delete(previousFilename);

        return toResponse(user);
    }

    public UserAccount requireUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    private UserAccount requireUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    public static UserResponse toResponse(UserAccount user) {
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                profilePictureUrl(user)
        );
    }

    private static String profilePictureUrl(UserAccount user) {
        if (user.getProfilePictureFilename() == null || user.getProfilePictureFilename().isBlank()) {
            return null;
        }
        return "/uploads/profile-pictures/" + user.getProfilePictureFilename();
    }
}
