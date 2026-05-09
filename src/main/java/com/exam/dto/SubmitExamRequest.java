package com.exam.dto;
import lombok.Data;
import java.util.List;

@Data
public class SubmitExamRequest {
    private List<AnswerItem> answers;

    @Data
    public static class AnswerItem {
        private String questionId;
        private String selectedOptionId;
    }
}
