package com.learning.learning_management_system.dto.CourseSchedule;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record CourseScheduleDtoCourseTime(
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate startDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			LocalDate endDate
) {
}