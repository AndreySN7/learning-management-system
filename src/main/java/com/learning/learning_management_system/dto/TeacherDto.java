package com.learning.learning_management_system.dto;

import jakarta.validation.constraints.NotBlank;

public record TeacherDto(
			@NotBlank
			String name,
			String surname
) {
}