package com.exam.service;

import com.exam.dto.UserDTO;
import com.exam.entity.User;
import com.exam.enums.Role;
import com.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public User createUser(UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername()))
            throw new RuntimeException("Username đã tồn tại: " + dto.getUsername());

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setRole(Role.valueOf(dto.getRole()));
        return userRepository.save(user);
    }

    public User updateUser(String id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(Role.valueOf(role.toUpperCase()));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
    }

    // Assign role (Admin only)
    public User assignRole(String userId, String role) {
        User user = getUserById(userId);
        user.setRole(Role.valueOf(role.toUpperCase()));
        return userRepository.save(user);
    }
}
