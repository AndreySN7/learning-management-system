package com.learning.learning_management_system.dto.student;

import java.util.HashSet;
import java.util.Set;

public record StudentDtoResponse(
			Long id,
			String name,
			String surname,
			Set<String> groups
) {
	public StudentDtoResponse {
		if (groups == null) {
			groups = new HashSet<>();
		}
	}
}
