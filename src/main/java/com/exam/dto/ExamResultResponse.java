package com.exam.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResultResponse {
    private String attemptId;
    private String examTitle;
    private double score;
    private int totalQuestions;
    private int correctCount;
    private LocalDateTime submitTime;
    private List<AnswerDetail> details;

    @Data
    public static class AnswerDetail {
        private String questionContent;
        private String selectedOption;
        private String correctOption;
        private boolean correct;
    }
}
