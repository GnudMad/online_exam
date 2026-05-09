package com.exam.controller;

import com.exam.dto.*;
import com.exam.entity.*;
import com.exam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/teacher")
@PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
@CrossOrigin(origins = "*")
public class TeacherController {

    @Autowired private QuestionService questionService;
    @Autowired private ExamService examService;
    @Autowired private GradeAndCourseService gradeCourseService;
    @Autowired private ReportService reportService;
    @Autowired private StudyMaterialService studyMaterialService;

    // ---- Questions ----
    @GetMapping("/questions")
    public ResponseEntity<?> getMyQuestions(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(questionService.getQuestionsByTeacher(
            examService.getTeacherIdByUsername(ud.getUsername())));
    }

    @GetMapping("/questions/course/{courseId}")
    public ResponseEntity<?> getQuestionsByCourse(@PathVariable String courseId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(questionService.getQuestionsByCourse(courseId)
            .stream().map(q -> questionService.toDTO(q, false)).toList());
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO dto,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(questionService.createQuestion(dto, ud.getUsername()));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable String id, @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, dto));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa câu hỏi"));
    }

    // ---- Exams ----
    @GetMapping("/exams")
    public ResponseEntity<?> getMyExams(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.getExamsByTeacher(ud.getUsername()));
    }

    @PostMapping("/exams")
    public ResponseEntity<?> createExam(@RequestBody ExamDTO dto,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.createExam(dto, ud.getUsername()));
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<?> updateExam(@PathVariable String id, @RequestBody ExamDTO dto) {
        return ResponseEntity.ok(examService.updateExam(id, dto));
    }

    @PutMapping("/exams/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable String id, @RequestBody Map<String,String> body) {
        return ResponseEntity.ok(examService.updateExamStatus(id, body.get("status")));
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable String id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa bài thi"));
    }

    @GetMapping("/exams/{id}/attempts")
    public ResponseEntity<?> getAttempts(@PathVariable String id) {
        return ResponseEntity.ok(examService.getExamAttempts(id));
    }

    @GetMapping("/exams/{id}/report")
    public ResponseEntity<?> getExamReport(@PathVariable String id) {
        return ResponseEntity.ok(reportService.generateClassReport(id));
    }

    @GetMapping("/exams/{id}/excel")
    public ResponseEntity<byte[]> exportExcel(@PathVariable String id) throws IOException {
        byte[] data = reportService.exportScoresToExcel(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diem_thi.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(data);
    }

    // ---- Study Materials ----
    @GetMapping("/materials")
    public ResponseEntity<?> getMyMaterials(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(studyMaterialService.getByTeacher(ud.getUsername()));
    }

    @PostMapping("/materials")
    public ResponseEntity<?> uploadMaterial(@RequestParam String title,
                                            @RequestParam String courseId,
                                            @RequestParam(required = false) MultipartFile file,
                                            @AuthenticationPrincipal UserDetails ud) throws IOException {
        return ResponseEntity.ok(studyMaterialService.create(title, courseId, file, ud.getUsername()));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable String id) {
        studyMaterialService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Đã xóa tài liệu"));
    }

    // ---- Courses (read) ----
    @GetMapping("/courses")
    public ResponseEntity<?> getCourses() {
        return ResponseEntity.ok(gradeCourseService.getAllCourses());
    }

    @Autowired private TabSwitchService tabSwitchService;

    /** Xem log chuyển tab của 1 bài thi - toàn bộ học sinh */
    @GetMapping("/exams/{id}/tab-switch-logs")
    public ResponseEntity<?> getTabSwitchLogs(@PathVariable String id) {
        return ResponseEntity.ok(tabSwitchService.getSummaryForExam(id));
    }

    /** Xem log chuyển tab chi tiết của 1 lần thi */
    @GetMapping("/attempts/{attemptId}/tab-switch-logs")
    public ResponseEntity<?> getAttemptLogs(@PathVariable String attemptId) {
        return ResponseEntity.ok(tabSwitchService.getLogsForAttempt(attemptId));
    }

}