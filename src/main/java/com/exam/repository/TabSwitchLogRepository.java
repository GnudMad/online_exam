package com.exam.repository;
import com.exam.entity.TabSwitchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TabSwitchLogRepository extends JpaRepository<TabSwitchLog, String> {
    // FIX: sort theo switchTime tăng dần → STT đúng thứ tự 1, 2, 3...
    List<TabSwitchLog> findByAttemptIdOrderBySwitchTimeAsc(String attemptId);
    long countByAttemptId(String attemptId);
}
