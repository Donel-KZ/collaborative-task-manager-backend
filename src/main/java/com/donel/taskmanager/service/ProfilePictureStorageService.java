package com.donel.taskmanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfilePictureStorageService {

    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private final Path uploadDirectory;

    public ProfilePictureStorageService(@Value("${app.profile-pictures.upload-dir:uploads/profile-pictures}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) {
        validate(file);

        String extension = extensionFor(file);
        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename).normalize();
        if (!destination.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid profile picture filename.");
        }

        try {
            Files.createDirectories(uploadDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return filename;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not store profile picture.", exception);
        }
    }

    public void delete(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }

        Path file = uploadDirectory.resolve(filename).normalize();
        if (!file.startsWith(uploadDirectory)) {
            return;
        }

        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile picture file is required.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException("Profile picture must be 5MB or smaller.");
        }
        if (!EXTENSIONS_BY_CONTENT_TYPE.containsKey(file.getContentType())) {
            throw new IllegalArgumentException("Profile picture must be a JPG, PNG, WEBP, or GIF image.");
        }
    }

    private String extensionFor(MultipartFile file) {
        String contentType = file.getContentType();
        String extension = EXTENSIONS_BY_CONTENT_TYPE.get(contentType);
        if (extension != null) {
            return extension;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex < 0) {
            return ".jpg";
        }

        String originalExtension = originalFilename.substring(dotIndex).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(originalExtension) ? originalExtension : ".jpg";
    }
}
