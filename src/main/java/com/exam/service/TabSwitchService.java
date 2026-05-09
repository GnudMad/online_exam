package com.exam.service;

import com.exam.dto.TabSwitchDTO;
import com.exam.entity.ExamAttempt;
import com.exam.entity.TabSwitchLog;
import com.exam.repository.ExamAttemptRepository;
import com.exam.repository.TabSwitchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TabSwitchService {

    @Autowired private TabSwitchLogRepository tabSwitchLogRepository;
    @Autowired private ExamAttemptRepository attemptRepository;

    public TabSwitchLog logSwitch(String attemptId, TabSwitchDTO dto) {
        ExamAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lần thi"));
        if (attempt.isSubmitted()) return null;

        // FIX: đếm số log hiện tại để switchNumber đúng
        long count = tabSwitchLogRepository.countByAttemptId(attemptId);

        TabSwitchLog log = new TabSwitchLog();
        log.setAttempt(attempt);
        log.setSwitchNumber((int) count + 1);
        // description từ client (tên tab đích nếu detect được)
        log.setDescription(dto != null && dto.getDescription() != null
                ? dto.getDescription() : "Rời khỏi trang thi");
        return tabSwitchLogRepository.save(log);
    }

    /** Lấy logs cho 1 attempt - sorted theo thời gian, STT từ 1→n */
    public List<Map<String, Object>> getLogsForAttempt(String attemptId) {
        // FIX: dùng sorted query
        List<TabSwitchLog> logs = tabSwitchLogRepository
                .findByAttemptIdOrderBySwitchTimeAsc(attemptId);

        // Re-assign STT theo thứ tự thật (tránh lỗi số bị lộn)
        for (int i = 0; i < logs.size(); i++) {
            logs.get(i).setSwitchNumber(i + 1);
        }

        return logs.stream().map(l -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", l.getId());
            m.put("switchNumber", l.getSwitchNumber());
            m.put("description", l.getDescription());
            m.put("switchTime", l.getSwitchTime());
            return m;
        }).collect(Collectors.toList());
    }

    /** Tổng hợp cho toàn bài thi */
    public List<Map<String, Object>> getSummaryForExam(String examId) {
        List<ExamAttempt> attempts = attemptRepository.findSubmittedByExamId(examId);
        return attempts.stream().map(a -> {
            long switchCount = tabSwitchLogRepository.countByAttemptId(a.getId());
            List<TabSwitchLog> logs = tabSwitchLogRepository
                    .findByAttemptIdOrderBySwitchTimeAsc(a.getId());

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("attemptId", a.getId());
            m.put("studentName", a.getStudent().getFullName());
            m.put("username", a.getStudent().getUsername());
            m.put("score", a.getScore());
            m.put("totalSwitches", switchCount);
            m.put("logs", logs.stream().map((l) -> {
                Map<String, Object> lm = new LinkedHashMap<>();
                lm.put("no", l.getSwitchNumber());
                lm.put("time", l.getSwitchTime());
                lm.put("description", l.getDescription());
                return lm;
            }).collect(Collectors.toList()));
            return m;
        }).collect(Collectors.toList());
    }
}
