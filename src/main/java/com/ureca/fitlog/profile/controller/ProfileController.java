package com.ureca.fitlog.profile.controller;

import com.ureca.fitlog.profile.dto.ProfileRequestDTO;
import com.ureca.fitlog.profile.dto.ProfileResponseDTO;
import com.ureca.fitlog.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileService profileService;

    /** 프로필 조회 */
    @GetMapping("/me")
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
    public ResponseEntity<ProfileResponseDTO> updateProfile(Authentication authentication, @RequestBody ProfileRequestDTO request) {
        String loginId = authentication.getName();
        ProfileResponseDTO updatedProfile = profileService.updateProfile(loginId, request);

        if(updatedProfile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedProfile);
    }
}
