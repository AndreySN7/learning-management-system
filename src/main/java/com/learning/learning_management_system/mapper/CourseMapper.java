package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.course.CourseDto;
import com.learning.learning_management_system.dto.course.CourseDtoResponse;
import com.learning.learning_management_system.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {
	CourseDtoResponse toDto(Course course);

	@Mapping(target = "id", ignore = true)
	Course toEntity(CourseDto courseDto);
}
