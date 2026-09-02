package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.StudentDto;
import com.learning.learning_management_system.entity.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudentMapper {
	@Mapping(target = "groups", ignore = true)
	StudentDto toDto(Student student);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "groups", ignore = true)
	Student toEntity(StudentDto studentDto);
}