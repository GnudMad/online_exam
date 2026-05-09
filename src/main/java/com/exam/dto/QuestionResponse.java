package com.exam.dto;
import lombok.Data;
import java.util.List;

@Data
public class QuestionResponse {
    private String id;
    private String content;
    private String difficulty;
    private String courseId;
    private String courseName;
    private List<OptionDto> options;

    @Data
    public static class OptionDto {
        private String id;
        private String content;
        private String label;
        private boolean correct;
    }
}
