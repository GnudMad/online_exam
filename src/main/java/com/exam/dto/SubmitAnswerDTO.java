package com.exam.dto;
import lombok.Data;
import java.util.Map;

@Data
public class SubmitAnswerDTO {
    // questionId -> selectedOptionId
    private Map<String, String> answers;
}
