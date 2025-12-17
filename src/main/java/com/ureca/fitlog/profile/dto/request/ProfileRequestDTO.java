package com.ureca.fitlog.profile.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileRequestDTO {
    private String username;
    private LocalDate birthDate;
    private String bio;
}
