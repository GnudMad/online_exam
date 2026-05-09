
package com.exam.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class QuestionDTO {
    private String id;
    private String content;
    private String difficulty;
    private String courseId;
    private List<AnswerOptionDTO> options;

    @Data
    public static class AnswerOptionDTO {
        private String id;
        private String content;
        
        @JsonProperty("isCorrect")
        private boolean correct;
        
        private String label;
        
        // Nếu cần method helper
        public boolean isCorrect() {
            return correct;
        }
        
        public void setCorrect(boolean correct) {
            this.correct = correct;
        }
    }
}