package com.exam.repository;
import com.exam.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, String> {
    Optional<Grade> findByName(String name);
}
