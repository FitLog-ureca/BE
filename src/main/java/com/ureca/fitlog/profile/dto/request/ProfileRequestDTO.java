package com.ureca.fitlog.profile.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequestDTO {
    private String name;
    private LocalDate birthDate;
    private String bio;
    private String profileImage;
}
