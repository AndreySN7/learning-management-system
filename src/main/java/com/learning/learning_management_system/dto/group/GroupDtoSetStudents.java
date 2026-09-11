package com.learning.learning_management_system.dto.group;

import java.util.HashSet;
import java.util.Set;

public record GroupDtoSetStudents(
			Set<Long> studentsIds
) {
	public GroupDtoSetStudents {
		if (studentsIds == null) {
			studentsIds = new HashSet<>();
		}
	}
}