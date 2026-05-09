package com.exam;

import com.exam.entity.*;
import com.exam.enums.Role;
import com.exam.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class OnlineExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExamApplication.class, args);
    }

    /**
     * Seed initial data on startup
     */
    @Bean
    CommandLineRunner seedData(UserRepository userRepo,
                               GradeRepository gradeRepo,
                               CourseRepository courseRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // Seed Admin
            if (!userRepo.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin123"));
                admin.setFullName("Quản Trị Viên");
                admin.setEmail("admin@school.edu.vn");
                admin.setRole(Role.ADMIN);
                userRepo.save(admin);
            }

            // Seed Teacher
            if (!userRepo.existsByUsername("teacher1")) {
                User teacher = new User();
                teacher.setUsername("teacher1");
                teacher.setPassword(encoder.encode("teacher123"));
                teacher.setFullName("Nguyễn Văn Giáo");
                teacher.setEmail("teacher1@school.edu.vn");
                teacher.setRole(Role.TEACHER);
                userRepo.save(teacher);
            }

            // Seed Student
            if (!userRepo.existsByUsername("student1")) {
                User student = new User();
                student.setUsername("student1");
                student.setPassword(encoder.encode("student123"));
                student.setFullName("Trần Thị Học");
                student.setEmail("student1@school.edu.vn");
                student.setRole(Role.STUDENT);
                userRepo.save(student);
            }

            // Seed Grades
            if (gradeRepo.count() == 0) {
                for (int i = 10; i <= 12; i++) {
                    Grade grade = new Grade();
                    grade.setName("Khối " + i);
                    gradeRepo.save(grade);
                }
            }

            // Seed Courses
            if (courseRepo.count() == 0) {
                Grade grade10 = gradeRepo.findByName("Khối 10").orElse(null);
                String[] subjects = {"Toán", "Vật Lý", "Hóa Học", "Sinh Học", "Ngữ Văn", "Tiếng Anh"};
                if (grade10 != null) {
                    for (String subject : subjects) {
                        Course course = new Course();
                        course.setName(subject);
                        course.setGrade(grade10);
                        courseRepo.save(course);
                    }
                }
            }

            System.out.println("===========================================");
            System.out.println(" Online Exam System started successfully!");
            System.out.println(" http://localhost:8080");
            System.out.println("===========================================");
        };
    }
}
