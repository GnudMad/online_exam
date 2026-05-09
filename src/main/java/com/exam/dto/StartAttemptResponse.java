package com.exam.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class StartAttemptResponse {
    private String attemptId;
    private String examTitle;
    private int duration; // seconds remaining
    private List<QuestionForExam> questions;

    @Data
    public static class QuestionForExam {
        private String id;
        private String content;
        private List<OptionForExam> options;
    }

    @Data
    public static class OptionForExam {
        private String id;
        private String content;
        private String label;
    }
}
