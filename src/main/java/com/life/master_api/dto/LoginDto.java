package com.life.master_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginDto {
    @NotEmpty(message = "Username or email cannot be empty")
    private String usernameOrEmail;

    @NotEmpty(message = "Password cannot be empty")
    private String password;
}