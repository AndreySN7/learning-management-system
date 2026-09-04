package com.learning.learning_management_system.dto;

import jakarta.validation.constraints.NotBlank;

public record TeacherDto(
			@NotBlank(message = "Value can not be empty")
			String name,
			String surname
) {
}