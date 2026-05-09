package com.exam.dto;
import lombok.Data;
import java.util.List;

@Data
public class QuestionRequest {
    private String content;
    private String difficulty;
    private String courseId;
    private List<OptionRequest> options;

    @Data
    public static class OptionRequest {
        private String content;
        private boolean isCorrect;
        private String label;
    }
}
