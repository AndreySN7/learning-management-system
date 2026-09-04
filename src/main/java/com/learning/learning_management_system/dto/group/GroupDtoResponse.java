package com.learning.learning_management_system.dto.group;

import java.util.HashSet;
import java.util.Set;

public record GroupDtoResponse(
			Long id,
			String groupName,
			Set<String> students
) {
	public GroupDtoResponse {
		if (students == null) {
			students = new HashSet<>();
		}
	}
}