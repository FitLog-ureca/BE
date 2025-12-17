package com.ureca.fitlog.profile.controller;

import com.ureca.fitlog.profile.dto.request.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.response.ProfileResponseDTO;
import com.ureca.fitlog.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Profile API", description = "프로필 관련 API")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    /** 프로필 조회 */
    @GetMapping("/me")
    @Operation(summary = "프로필 조회")
    public ResponseEntity<ProfileResponseDTO> getProfile(Authentication authentication) {
        String loginId = authentication.getName();
        log.info("[PROFILE-GET] loginId={}", loginId);
        ProfileResponseDTO profile = profileService.getProfileByLoginId(loginId);

        return ResponseEntity.ok(profile);
    }

    /** 프로필 수정 */
    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "프로필 수정",
            description = "프로필 정보 및 이미지를 수정합니다."
    )
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            Authentication authentication,
            @ModelAttribute ProfileUpdateRequest updateRequest
    ) {
        String loginId = authentication.getName();

        // DTO 변환
        ProfileRequestDTO request = new ProfileRequestDTO();
        request.setUsername(updateRequest.getUsername());
        request.setBirthDate(updateRequest.getBirthDate());
        request.setBio(updateRequest.getBio());

        ProfileResponseDTO updatedProfile = profileService.updateProfile(
                loginId,
                request,
                updateRequest.getProfileImage()
        );

        return ResponseEntity.ok(updatedProfile);
    }

    @Data
    @Schema(description = "프로필 수정 요청")
    public static class ProfileUpdateRequest {
        @Schema(description = "사용자 이름")
        private String username;

        @Schema(description = "생년월일", example = "1990-01-01")
        private LocalDate birthDate;

        @Schema(description = "자기소개")
        private String bio;

        @Schema(description = "프로필 이미지 (JPEG, PNG, JPG, WEBP, 최대 5MB)", type = "string", format = "binary")
        private MultipartFile profileImage;
    }
}