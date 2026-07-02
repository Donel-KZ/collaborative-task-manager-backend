package com.donel.taskmanager.controller;

import com.donel.taskmanager.dto.UserResponse;
import com.donel.taskmanager.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/me")
    public UserResponse findCurrentUser(Authentication authentication) {
        return userService.findCurrentUser(authentication.getName());
    }

    @PatchMapping(
            value = "/me/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserResponse updateProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
    ) {
        System.out.println("UPLOAD ENDPOINT HIT");
        System.out.println(authentication);

        return userService.updateProfilePicture(authentication.getName(), file);
    }

}
