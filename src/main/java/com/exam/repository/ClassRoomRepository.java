package com.exam.repository;
import com.exam.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, String> {
    List<ClassRoom> findByGradeId(String gradeId);
}
