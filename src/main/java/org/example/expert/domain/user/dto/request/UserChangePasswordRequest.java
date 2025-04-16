package org.example.expert.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserChangePasswordRequest {

    @Size(min = 8)
    @Pattern(regexp = "^[0-9A-Za-z]$")
    @NotBlank
    private String oldPassword;

    @Size(min = 8)
    @Pattern(regexp = "^[0-9A-Za-z]$")
    @NotBlank
    private String newPassword;
}
