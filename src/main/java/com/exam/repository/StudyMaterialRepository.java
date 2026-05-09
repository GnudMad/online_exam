package com.exam.repository;
import com.exam.entity.StudyMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, String> {
    List<StudyMaterial> findByCourseId(String courseId);
    List<StudyMaterial> findByTeacherId(String teacherId);
}
