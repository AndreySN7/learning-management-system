package com.learning.learning_management_system.dto.CourseSchedule;

import jakarta.validation.constraints.Positive;

import java.util.Set;

public record CourseScheduleDtoGroupToCourse(
			@Positive(message = "Value can not be empty")
			Long teacherId,
			Set<Long> coursesIds
) {
}