package com.learning.learning_management_system.dto.group;

import jakarta.validation.constraints.NotBlank;

public record GroupDtoGroupName(
			@NotBlank(message = "Value can not be empty")
			String groupName
) {
}