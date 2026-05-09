package com.exam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "tab_switch_logs")
@Data
@NoArgsConstructor
public class TabSwitchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private ExamAttempt attempt;

    @Column(nullable = false)
    private LocalDateTime switchTime = LocalDateTime.now();

    /** Mô tả hành động chuyển tab từ client */
    @Column(length = 200)
    private String description;

    /** Số lần chuyển tab tính đến thời điểm này */
    private int switchNumber;
}
