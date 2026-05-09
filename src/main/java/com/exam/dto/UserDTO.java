package com.exam.dto;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role;
}
