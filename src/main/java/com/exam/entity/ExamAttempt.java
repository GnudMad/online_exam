package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exam_attempts")
@Data
@NoArgsConstructor
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private LocalDateTime startTime = LocalDateTime.now();

    private LocalDateTime submitTime;
    private double score = 0.0;
    private boolean submitted = false;

    @JsonIgnore
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<StudentAnswer> answers;

    @JsonIgnore
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<TabSwitchLog> tabSwitchLogs;

    public double calculateScore() {
        if (answers == null || answers.isEmpty()) return 0.0;
        long correct = answers.stream().filter(StudentAnswer::isCorrect).count();
        return Math.round((double) correct / answers.size() * 10.0 * 100.0) / 100.0;
    }
}
