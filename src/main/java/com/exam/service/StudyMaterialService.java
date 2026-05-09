package com.exam.service;

import com.exam.entity.*;
import com.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class StudyMaterialService {

    @Autowired private StudyMaterialRepository materialRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CourseRepository courseRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public StudyMaterial create(String title, String courseId,
                                 MultipartFile file, String teacherUsername) throws IOException {
        User teacher = userRepository.findByUsername(teacherUsername).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            Path uploadPath = Paths.get(uploadDir);
            Files.createDirectories(uploadPath);
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), uploadPath.resolve(filename),
                       StandardCopyOption.REPLACE_EXISTING);
            fileUrl = "/uploads/" + filename;
        }

        StudyMaterial material = new StudyMaterial();
        material.setTitle(title);
        material.setFileUrl(fileUrl);
        material.setTeacher(teacher);
        material.setCourse(course);
        return materialRepository.save(material);
    }

    public List<StudyMaterial> getByCourse(String courseId) {
        return materialRepository.findByCourseId(courseId);
    }

    public List<StudyMaterial> getByTeacher(String username) {
        User teacher = userRepository.findByUsername(username).orElseThrow();
        return materialRepository.findByTeacherId(teacher.getId());
    }

    public void delete(String id) {
        materialRepository.deleteById(id);
    }
}
