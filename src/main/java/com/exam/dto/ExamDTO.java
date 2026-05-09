package com.exam.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDTO {
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
    private List<String> questionIds;
}
