package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.student.StudentDto;
import com.learning.learning_management_system.dto.student.StudentDtoResponse;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {
	@Mapping(target = "groups", ignore = true)
	StudentDtoResponse toDto(Student student);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "groups", ignore = true)
	Student toEntity(StudentDto studentDto);

	default StudentDtoResponse toFullDto(Student student) {
		Set<String> studentGroups = student.getGroups().stream()
					.map(Group::getGroupName)
					.collect(Collectors.toSet());

		StudentDtoResponse dto = toDto(student);
		dto.groups().addAll(studentGroups);
		return dto;
	}
}