package edu.srmist.leave.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class LeaveWorkers {
    private static final Logger log = LoggerFactory.getLogger(LeaveWorkers.class);

    @JobWorker(type = "validate-leave")
    public Map<String, Object> validateLeave(
            @Variable String fromDate,
            @Variable String toDate) {

        Map<String, Object> result = new HashMap<>();
        boolean valid = false;
        long days = 0;

        try {
            LocalDate from = LocalDate.parse(fromDate);
            LocalDate to = LocalDate.parse(toDate);
            days = ChronoUnit.DAYS.between(from, to) + 1;
            valid = days > 0 && !from.isBefore(LocalDate.now());
        } catch (DateTimeParseException | NullPointerException ex) {
            log.warn("Leave request contains a missing or invalid date.");
        }

        result.put("leaveDays", days);
        result.put("valid", valid);
        result.put("approved", valid && days <= 2);
        return result;
    }

    @JobWorker(type = "notify-student")
    public Map<String, Object> notifyStudent(
            @Variable String studentName,
            @Variable String regNo,
            @Variable Boolean approved,
            @Variable String remarks) {

        String status = Boolean.TRUE.equals(approved) ? "APPROVED" : "REJECTED";
        log.info("Notification to {} ({}): leave request {}. Remarks: {}",
                studentName, regNo, status, remarks == null ? "No remarks" : remarks);

        return Map.of("leaveStatus", status);
    }
}
