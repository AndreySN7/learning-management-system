package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.TeacherDto;
import com.learning.learning_management_system.entity.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

	TeacherDto toDto(Teacher teacher);

	@Mapping(target = "id", ignore = true)
	Teacher toEntity(TeacherDto teacherDto);
}
