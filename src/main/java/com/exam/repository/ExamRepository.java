package com.exam.repository;
import com.exam.entity.Exam;
import com.exam.enums.ExamStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
    List<Exam> findByTeacherId(String teacherId);
    List<Exam> findByCourseId(String courseId);
    List<Exam> findByStatus(ExamStatus status);
}
