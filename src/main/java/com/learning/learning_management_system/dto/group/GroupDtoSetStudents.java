package com.learning.learning_management_system.dto.group;

import java.util.Set;

public record GroupDtoSetStudents(
			Set<Long> studentsIds
) {
}