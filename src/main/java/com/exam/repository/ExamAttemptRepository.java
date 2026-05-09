package com.exam.repository;
import com.exam.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, String> {
    List<ExamAttempt> findByStudentId(String studentId);
    List<ExamAttempt> findByExamId(String examId);
    Optional<ExamAttempt> findByExamIdAndStudentIdAndSubmittedFalse(String examId, String studentId);
    boolean existsByExamIdAndStudentId(String examId, String studentId);

    // FIX: kiểm tra đã nộp bài
    boolean existsByExamIdAndStudentIdAndSubmittedTrue(String examId, String studentId);

    @Query("SELECT a FROM ExamAttempt a WHERE a.exam.id = :examId AND a.submitted = true")
    List<ExamAttempt> findSubmittedByExamId(String examId);
}
