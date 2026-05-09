package com.exam.service;

import com.exam.dto.ExamDTO;
import com.exam.dto.ExamResultDTO;
import com.exam.dto.SubmitAnswerDTO;
import com.exam.entity.*;
import com.exam.enums.ExamStatus;
import com.exam.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired private ExamRepository examRepository;
    @Autowired private ExamAttemptRepository attemptRepository;
    @Autowired private StudentAnswerRepository studentAnswerRepository;
    @Autowired private TabSwitchLogRepository tabSwitchLogRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerOptionRepository answerOptionRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private UserRepository userRepository;

    // ==================== TEACHER METHODS ====================

    @Transactional
    public Exam createExam(ExamDTO dto, String teacherUsername) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên"));

        Exam exam = new Exam();
        exam.setTitle(dto.getTitle());
        exam.setDuration(dto.getDuration());
        exam.setQuestionCount(dto.getQuestionCount());
        exam.setCourse(course);
        exam.setTeacher(teacher);
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());
        exam.setStatus(ExamStatus.DRAFT);

        // Assign questions if provided
        if (dto.getQuestionIds() != null && !dto.getQuestionIds().isEmpty()) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            exam.setQuestions(questions);
        }

        return examRepository.save(exam);
    }

    public Exam updateExamStatus(String examId, String status) {
        Exam exam = getExamById(examId);
        exam.setStatus(ExamStatus.valueOf(status.toUpperCase()));
        return examRepository.save(exam);
    }

    public Exam updateExam(String examId, ExamDTO dto) {
        Exam exam = getExamById(examId);
        exam.setTitle(dto.getTitle());
        exam.setDuration(dto.getDuration());
        exam.setQuestionCount(dto.getQuestionCount());
        exam.setStartTime(dto.getStartTime());
        exam.setEndTime(dto.getEndTime());

        if (dto.getQuestionIds() != null) {
            List<Question> questions = questionRepository.findAllById(dto.getQuestionIds());
            exam.setQuestions(questions);
        }
        return examRepository.save(exam);
    }

    public void deleteExam(String examId) {
        examRepository.deleteById(examId);
    }

    public List<Exam> getExamsByTeacher(String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giáo viên"));
        return examRepository.findByTeacherId(teacher.getId());
    }

    // ==================== STUDENT METHODS ====================

    public List<Exam> getOpenExams() {
        return examRepository.findByStatus(ExamStatus.OPEN);
    }

    @Transactional
    public Map<String, Object> startExam(String examId, String studentUsername) {
        Exam exam = getExamById(examId);
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh"));

        if (!exam.isAvailable())
            throw new RuntimeException("Bài kiểm tra chưa mở hoặc đã kết thúc");

        // FIX: Kiểm tra đã nộp bài rồi → không cho làm lại
        boolean alreadySubmitted = attemptRepository.existsByExamIdAndStudentIdAndSubmittedTrue(examId, student.getId());
        if (alreadySubmitted)
            throw new RuntimeException("BAN_DA_NOI_BAI");

        // Check if already has an in-progress attempt
        Optional<ExamAttempt> existing = attemptRepository
                .findByExamIdAndStudentIdAndSubmittedFalse(examId, student.getId());
        if (existing.isPresent())
            return buildExamSession(exam, existing.get());

        // Create new attempt
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setStudent(student);
        attempt.setStartTime(LocalDateTime.now());
        attemptRepository.save(attempt);

        return buildExamSession(exam, attempt);
    }

    private Map<String, Object> buildExamSession(Exam exam, ExamAttempt attempt) {
        List<Question> questions = exam.getQuestions();
        // Shuffle questions for each student
        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled);

        Map<String, Object> session = new HashMap<>();
        session.put("attemptId", attempt.getId());
        session.put("examTitle", exam.getTitle());
        session.put("duration", exam.getDuration());
        session.put("startTime", attempt.getStartTime());
        session.put("questions", shuffled.stream().map(q -> {
            Map<String, Object> qMap = new LinkedHashMap<>();
            qMap.put("id", q.getId());
            qMap.put("content", q.getContent());
            // Shuffle options too, hide correct answer
            List<Map<String, String>> opts = new ArrayList<>();
            if (q.getOptions() != null) {
                List<AnswerOption> shuffledOpts = new ArrayList<>(q.getOptions());
                Collections.shuffle(shuffledOpts);
                for (AnswerOption opt : shuffledOpts) {
                    Map<String, String> o = new LinkedHashMap<>();
                    o.put("id", opt.getId());
                    o.put("content", opt.getContent());
                    o.put("label", opt.getLabel());
                    opts.add(o);
                }
            }
            qMap.put("options", opts);
            return qMap;
        }).collect(Collectors.toList()));
        return session;
    }

    @Transactional
    public ExamResultDTO submitExam(String attemptId, SubmitAnswerDTO dto) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lần thi"));

        if (attempt.isSubmitted())
            throw new RuntimeException("Bài thi đã được nộp");

        // Save each answer
        List<StudentAnswer> savedAnswers = new ArrayList<>();
        if (dto.getAnswers() != null) {
            for (Map.Entry<String, String> entry : dto.getAnswers().entrySet()) {
                Question q = questionRepository.findById(entry.getKey()).orElse(null);
                AnswerOption opt = answerOptionRepository.findById(entry.getValue()).orElse(null);
                if (q != null) {
                    StudentAnswer sa = new StudentAnswer();
                    sa.setAttempt(attempt);
                    sa.setQuestion(q);
                    sa.setSelectedOption(opt);
                    savedAnswers.add(studentAnswerRepository.save(sa));
                }
            }
        }

        attempt.setAnswers(savedAnswers);
        attempt.setSubmitted(true);
        attempt.setSubmitTime(LocalDateTime.now());
        double score = attempt.calculateScore();
        attempt.setScore(score);
        attemptRepository.save(attempt);

        return buildResult(attempt, savedAnswers);
    }

    public ExamResultDTO getExamResult(String attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lần thi"));
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);
        return buildResult(attempt, answers);
    }

    private ExamResultDTO buildResult(ExamAttempt attempt, List<StudentAnswer> answers) {
        ExamResultDTO result = new ExamResultDTO();
        result.setAttemptId(attempt.getId());
        result.setExamTitle(attempt.getExam().getTitle());
        result.setScore(attempt.getScore());
        result.setTotalQuestions(answers.size());
        result.setCorrectAnswers((int) answers.stream().filter(StudentAnswer::isCorrect).count());
        result.setSubmitTime(attempt.getSubmitTime());

        List<ExamResultDTO.QuestionResultDTO> qResults = answers.stream().map(sa -> {
            ExamResultDTO.QuestionResultDTO qr = new ExamResultDTO.QuestionResultDTO();
            qr.setQuestionContent(sa.getQuestion().getContent());
            qr.setCorrect(sa.isCorrect());
            if (sa.getSelectedOption() != null)
                qr.setSelectedOption(sa.getSelectedOption().getContent());
            // Find correct option
            sa.getQuestion().getOptions().stream()
                    .filter(AnswerOption::isCorrect)
                    .findFirst()
                    .ifPresent(o -> qr.setCorrectOption(o.getContent()));
            return qr;
        }).collect(Collectors.toList());
        result.setQuestionResults(qResults);
        return result;
    }

    public List<ExamAttempt> getStudentHistory(String studentUsername) {
        User student = userRepository.findByUsername(studentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh"));
        return attemptRepository.findByStudentId(student.getId());
    }

    public List<ExamAttempt> getExamAttempts(String examId) {
        return attemptRepository.findSubmittedByExamId(examId);
    }

    // Log tab switch
    @Transactional
    public void logTabSwitch(String attemptId) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lần thi"));
        if (!attempt.isSubmitted()) {
            TabSwitchLog log = new TabSwitchLog();
            log.setAttempt(attempt);
            tabSwitchLogRepository.save(log);
        }
    }

    public Exam getExamById(String id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài thi"));
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public String getTeacherIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"))
                .getId();
    }
}