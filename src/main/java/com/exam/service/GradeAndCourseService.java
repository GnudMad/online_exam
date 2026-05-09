package com.exam.service;

import com.exam.entity.Course;
import com.exam.entity.Grade;
import com.exam.repository.CourseRepository;
import com.exam.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeAndCourseService {

    @Autowired private GradeRepository gradeRepository;
    @Autowired private CourseRepository courseRepository;

    // ---- Grade ----
    public Grade createGrade(String name) {
        Grade grade = new Grade();
        grade.setName(name);
        return gradeRepository.save(grade);
    }

    public Grade updateGrade(String id, String name) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khối"));
        grade.setName(name);
        return gradeRepository.save(grade);
    }

    public void deleteGrade(String id) {
        gradeRepository.deleteById(id);
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Grade getGradeById(String id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khối"));
    }

    // ---- Course ----
    public Course createCourse(String name, String gradeId) {
        Grade grade = getGradeById(gradeId);
        Course course = new Course();
        course.setName(name);
        course.setGrade(grade);
        return courseRepository.save(course);
    }

    public Course updateCourse(String id, String name, String gradeId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        course.setName(name);
        if (gradeId != null) course.setGrade(getGradeById(gradeId));
        return courseRepository.save(course);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByGrade(String gradeId) {
        return courseRepository.findByGradeId(gradeId);
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
    }
}
