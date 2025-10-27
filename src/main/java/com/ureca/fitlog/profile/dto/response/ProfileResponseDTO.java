package com.ureca.fitlog.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {

    private String name;
    private int age;
    private String bio;
    private String profileImage;
    private LocalDate birthDate;

    // birthDate를 기반으로 age 자동 계산
    public void calculateAgeFromBirthDate() {
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        }
    }
}
