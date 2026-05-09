package com.exam.controller;

import com.exam.dto.UserDTO;
import com.exam.entity.User;
import com.exam.service.GradeAndCourseService;
import com.exam.service.ReportService;
import com.exam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private GradeAndCourseService gradeCourseService;
    @Autowired private ReportService reportService;

    // ---- Users ----
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa người dùng"));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> assignRole(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.assignRole(id, body.get("role")));
    }

    // ---- Grades ----
    @GetMapping("/grades")
    public ResponseEntity<?> getGrades() {
        return ResponseEntity.ok(gradeCourseService.getAllGrades());
    }

    @PostMapping("/grades")
    public ResponseEntity<?> createGrade(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(gradeCourseService.createGrade(body.get("name")));
    }

    @PutMapping("/grades/{id}")
    public ResponseEntity<?> updateGrade(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(gradeCourseService.updateGrade(id, body.get("name")));
    }

    @DeleteMapping("/grades/{id}")
    public ResponseEntity<?> deleteGrade(@PathVariable String id) {
        gradeCourseService.deleteGrade(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa khối"));
    }

    // ---- Courses ----
    @GetMapping("/courses")
    public ResponseEntity<?> getCourses() {
        return ResponseEntity.ok(gradeCourseService.getAllCourses());
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(gradeCourseService.createCourse(body.get("name"), body.get("gradeId")));
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(gradeCourseService.updateCourse(id, body.get("name"), body.get("gradeId")));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable String id) {
        gradeCourseService.deleteCourse(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa môn học"));
    }

    // ---- Reports ----
    @GetMapping("/reports/school")
    public ResponseEntity<?> getSchoolReport() {
        return ResponseEntity.ok(reportService.generateSchoolReport());
    }

    @GetMapping("/reports/exam/{examId}")
    public ResponseEntity<?> getExamReport(@PathVariable String examId) {
        return ResponseEntity.ok(reportService.generateClassReport(examId));
    }

    @GetMapping("/reports/exam/{examId}/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable String examId) throws IOException {
        byte[] data = reportService.exportScoresToExcel(examId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=scores.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }
}
