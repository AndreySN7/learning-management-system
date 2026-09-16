package com.learning.learning_management_system.dto.CourseSchedule;

import java.time.LocalDate;

public record CourseScheduleDtoResponse(
			Long id,
			String groupName,
			String courseName,
			String teacherName,
			String teacherSurname,
			LocalDate startDate,
			LocalDate endDate
) {
}
