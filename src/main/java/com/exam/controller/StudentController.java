package com.exam.controller;

import com.exam.dto.*;
import com.exam.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasAnyRole('STUDENT','TEACHER','ADMIN')")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired private ExamService examService;
    @Autowired private StudyMaterialService studyMaterialService;
    @Autowired private GradeAndCourseService gradeCourseService;
    @Autowired private TabSwitchService tabSwitchService;

    @GetMapping("/exams")
    public ResponseEntity<?> getOpenExams() {
        return ResponseEntity.ok(examService.getOpenExams());
    }

    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<?> startExam(@PathVariable String examId,
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.startExam(examId, ud.getUsername()));
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<?> submitExam(@PathVariable String attemptId,
            @RequestBody SubmitAnswerDTO dto) {
        return ResponseEntity.ok(examService.submitExam(attemptId, dto));
    }

    @GetMapping("/attempts/{attemptId}/result")
    public ResponseEntity<?> getResult(@PathVariable String attemptId) {
        return ResponseEntity.ok(examService.getExamResult(attemptId));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(examService.getStudentHistory(ud.getUsername()));
    }

    /** Ghi log chuyển tab với mô tả chi tiết */
    @PostMapping("/attempts/{attemptId}/tab-switch")
    public ResponseEntity<?> logTabSwitch(@PathVariable String attemptId,
            @RequestBody(required = false) TabSwitchDTO dto) {
        tabSwitchService.logSwitch(attemptId, dto);
        return ResponseEntity.ok(Map.of("message", "Logged"));
    }

    @GetMapping("/materials/course/{courseId}")
    public ResponseEntity<?> getMaterials(@PathVariable String courseId) {
        return ResponseEntity.ok(studyMaterialService.getByCourse(courseId));
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getCourses() {
        return ResponseEntity.ok(gradeCourseService.getAllCourses());
    }
}
