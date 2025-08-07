package com.taskflow.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
public class UserDTO {
    @NotBlank(message = "username is required")
    private String username;
    @Size(min=6, message = "Password must be at least 6 character")
    private String password;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
}
