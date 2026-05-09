package com.exam.dto;
import com.exam.enums.Role;
import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role;
}
