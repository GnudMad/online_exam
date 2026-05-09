package com.exam.dto;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private boolean enabled;
}
