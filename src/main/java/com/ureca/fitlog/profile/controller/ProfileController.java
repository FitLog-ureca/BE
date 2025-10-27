package com.ureca.fitlog.profile.controller;

import com.ureca.fitlog.profile.dto.request.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.response.ProfileResponseDTO;
import com.ureca.fitlog.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
@Tag(name = "Profile API", description = "프로필 관련 API")
public class ProfileController {
    private final ProfileService profileService;

    /** 프로필 조회 */
    @GetMapping("/me")
    @Operation(
            summary = "프로필 조회"
    )
    public ResponseEntity<ProfileResponseDTO> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String loginId = authentication.getName();
        ProfileResponseDTO profile = profileService.getProfileByLoginId(loginId);
        return ResponseEntity.ok(profile);
    }

    /** 프로필 수정 */
    @PutMapping("/update")
    @Operation(
            summary = "프로필 수정"
    )
    public ResponseEntity<ProfileResponseDTO> updateProfile(Authentication authentication, @RequestBody ProfileRequestDTO request) {
        String loginId = authentication.getName();
        ProfileResponseDTO updatedProfile = profileService.updateProfile(loginId, request);

        if(updatedProfile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedProfile);
    }
}
