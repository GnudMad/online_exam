package com.exam.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResultDTO {
    private String attemptId;
    private String examTitle;
    private double score;
    private int totalQuestions;
    private int correctAnswers;
    private LocalDateTime submitTime;
    private List<QuestionResultDTO> questionResults;

    @Data
    public static class QuestionResultDTO {
        private String questionContent;
        private String selectedOption;
        private String correctOption;
        private boolean isCorrect;
    }
}
