package com.exam.service;

import com.exam.dto.QuestionDTO;
import com.exam.entity.*;
import com.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerOptionRepository answerOptionRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;

    @Transactional
    public Question createQuestion(QuestionDTO dto, String teacherUsername) {
        // Validate input
        if (dto.getContent() == null || dto.getContent().isBlank())
            throw new RuntimeException("Nội dung câu hỏi không được trống");
        if (dto.getCourseId() == null || dto.getCourseId().isBlank())
            throw new RuntimeException("Phải chọn môn học");
        if (dto.getOptions() == null || dto.getOptions().size() < 2)
            throw new RuntimeException("Câu hỏi phải có ít nhất 2 đáp án");

        boolean hasCorrect = dto.getOptions().stream()
                .anyMatch(QuestionDTO.AnswerOptionDTO::isCorrect);
        if (!hasCorrect)
            throw new RuntimeException("Phải chọn ít nhất 1 đáp án đúng");

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên"));

        // Lưu câu hỏi trước
        Question q = new Question();
        q.setContent(dto.getContent().trim());
        q.setDifficulty(dto.getDifficulty() != null ? dto.getDifficulty() : "MEDIUM");
        q.setCourse(course);
        q.setTeacher(teacher);
        // FIX: KHÔNG gọi setOptions() ở đây - để Hibernate quản lý
        Question saved = questionRepository.save(q);

        // Lưu từng đáp án riêng
        List<AnswerOption> savedOptions = new ArrayList<>();
        for (QuestionDTO.AnswerOptionDTO optDto : dto.getOptions()) {
            AnswerOption opt = new AnswerOption();
            opt.setQuestion(saved);
            opt.setContent(optDto.getContent() != null ? optDto.getContent().trim() : "");
            opt.setCorrect(optDto.isCorrect());
            opt.setLabel(optDto.getLabel());
            savedOptions.add(answerOptionRepository.save(opt));
        }

        // FIX: Reload từ DB để lấy list do Hibernate quản lý (tránh mutable list issue)
        return questionRepository.findById(saved.getId())
                .orElse(saved);
    }

    @Transactional
    public Question updateQuestion(String id, QuestionDTO dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));
        q.setContent(dto.getContent());
        if (dto.getDifficulty() != null) q.setDifficulty(dto.getDifficulty());

        // Xóa options cũ
        List<AnswerOption> oldOpts = new ArrayList<>(q.getOptions());
        answerOptionRepository.deleteAll(oldOpts);
        q.getOptions().clear();
        questionRepository.save(q);

        // Tạo options mới
        if (dto.getOptions() != null) {
            for (QuestionDTO.AnswerOptionDTO optDto : dto.getOptions()) {
                AnswerOption opt = new AnswerOption();
                opt.setQuestion(q);
                opt.setContent(optDto.getContent());
                opt.setCorrect(optDto.isCorrect());
                opt.setLabel(optDto.getLabel());
                answerOptionRepository.save(opt);
            }
        }
        return questionRepository.findById(id).orElse(q);
    }

    public void deleteQuestion(String id) {
        questionRepository.deleteById(id);
    }

    public List<Question> getQuestionsByCourse(String courseId) {
        return questionRepository.findByCourseId(courseId);
    }

    public List<Question> getQuestionsByTeacher(String teacherId) {
        return questionRepository.findByTeacherId(teacherId);
    }

    public List<Question> getRandomQuestions(String courseId, int count) {
        return questionRepository.findRandomByCourseId(courseId, PageRequest.of(0, count));
    }

    public Question getQuestionById(String id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi"));
    }

    public QuestionDTO toDTO(Question q, boolean hideAnswer) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setDifficulty(q.getDifficulty());
        if (q.getCourse() != null) dto.setCourseId(q.getCourse().getId());
        if (q.getOptions() != null) {
            dto.setOptions(q.getOptions().stream().map(opt -> {
                QuestionDTO.AnswerOptionDTO odto = new QuestionDTO.AnswerOptionDTO();
                odto.setId(opt.getId());
                odto.setContent(opt.getContent());
                odto.setLabel(opt.getLabel());
                if (!hideAnswer) odto.setCorrect(opt.isCorrect());
                return odto;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}
