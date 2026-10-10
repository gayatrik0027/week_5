package edu.srmist.leave.api;

public record LeaveRequest(
        String studentName,
        String regNo,
        String fromDate,
        String toDate,
        String reason
) {}
