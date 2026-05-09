package com.exam.service;

import com.exam.entity.*;
import com.exam.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired private ExamAttemptRepository attemptRepository;
    @Autowired private ExamRepository examRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TabSwitchLogRepository tabSwitchLogRepository;

    public Map<String, Object> generateSchoolReport() {
        List<Exam> exams = examRepository.findAll();
        List<Map<String, Object>> examStats = new ArrayList<>();
        for (Exam exam : exams) {
            List<ExamAttempt> attempts = attemptRepository.findSubmittedByExamId(exam.getId());
            if (attempts.isEmpty()) continue;
            double avg = attempts.stream().mapToDouble(ExamAttempt::getScore).average().orElse(0);
            double max = attempts.stream().mapToDouble(ExamAttempt::getScore).max().orElse(0);
            double min = attempts.stream().mapToDouble(ExamAttempt::getScore).min().orElse(0);
            Map<String, Object> stat = new LinkedHashMap<>();
            stat.put("examId", exam.getId());
            stat.put("examTitle", exam.getTitle());
            stat.put("courseName", exam.getCourse() != null ? exam.getCourse().getName() : "");
            stat.put("totalStudents", attempts.size());
            stat.put("averageScore", Math.round(avg * 100.0) / 100.0);
            stat.put("maxScore", max);
            stat.put("minScore", min);
            examStats.add(stat);
        }
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalExams", exams.size());
        report.put("examStats", examStats);
        return report;
    }

    /** Class report - bao gồm tab switch count */
    public Map<String, Object> generateClassReport(String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài thi"));
        List<ExamAttempt> attempts = attemptRepository.findSubmittedByExamId(examId);

        List<Map<String, Object>> studentScores = attempts.stream().map(a -> {
            long tabCount = tabSwitchLogRepository.countByAttemptId(a.getId());
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("attemptId", a.getId());
            s.put("studentId", a.getStudent().getId());
            s.put("studentName", a.getStudent().getFullName());
            s.put("username", a.getStudent().getUsername());
            s.put("score", a.getScore());
            s.put("submitTime", a.getSubmitTime());
            s.put("tabSwitchCount", tabCount);   // FIX: thêm tab count
            return s;
        }).sorted(Comparator.comparingDouble(m -> -((double) m.get("score"))))
          .collect(Collectors.toList());

        Map<String, Object> report = new LinkedHashMap<>();
        report.put("examTitle", exam.getTitle());
        report.put("totalStudents", attempts.size());
        report.put("scores", studentScores);
        return report;
    }

    public byte[] exportScoresToExcel(String examId) throws IOException {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài thi"));
        List<ExamAttempt> attempts = attemptRepository.findSubmittedByExamId(examId);

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Điểm - " + exam.getTitle());
            CellStyle hStyle = wb.createCellStyle();
            Font f = wb.createFont(); f.setBold(true); hStyle.setFont(f);
            hStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            String[] cols = {"STT","Họ tên","Username","Điểm","Chuyển tab","Thời gian nộp"};
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i); c.setCellValue(cols[i]); c.setCellStyle(hStyle);
                sheet.setColumnWidth(i, i == 0 ? 2000 : 6000);
            }
            for (int i = 0; i < attempts.size(); i++) {
                ExamAttempt a = attempts.get(i);
                long tabCount = tabSwitchLogRepository.countByAttemptId(a.getId());
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(a.getStudent().getFullName());
                row.createCell(2).setCellValue(a.getStudent().getUsername());
                row.createCell(3).setCellValue(a.getScore());
                row.createCell(4).setCellValue(tabCount);
                row.createCell(5).setCellValue(
                    a.getSubmitTime() != null ? a.getSubmitTime().toString() : "");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }
}
