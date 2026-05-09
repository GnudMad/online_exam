package com.exam.entity;

import com.exam.enums.ExamStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exams")
@Data
@NoArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private int questionCount;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExamStatus status = ExamStatus.DRAFT;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "exam_questions",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private List<Question> questions;

    @JsonIgnore
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamAttempt> attempts;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return status == ExamStatus.OPEN
                && (startTime == null || !now.isBefore(startTime))
                && (endTime == null || !now.isAfter(endTime));
    }
}
