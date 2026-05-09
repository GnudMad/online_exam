package com.exam.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ExamResponse {
    private String id;
    private String title;
    private int duration;
    private int questionCount;
    private String status;
    private String courseId;
    private String courseName;
    private String teacherName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
}
