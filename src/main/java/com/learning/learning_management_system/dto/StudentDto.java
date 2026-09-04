package com.learning.learning_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.HashSet;
import java.util.Set;

public record StudentDto(
			@NotBlank(message = "Value can not be empty")
			String name,
			String surname,
			@NotEmpty(message = "Group set cannot be empty")
			Set<String> groups
) {
	public StudentDto{
		if (groups == null) {
			groups = new HashSet<String>();
		}
	}
}