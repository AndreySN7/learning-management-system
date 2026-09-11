package com.learning.learning_management_system.mapper;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoResponse;
import com.learning.learning_management_system.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseScheduleMapper {

	@Mapping(source = "group.groupName", target = "groupName")
	@Mapping(source = "course.name", target = "courseName")
	@Mapping(source = "teacher.name", target = "teacherName")
	@Mapping(source = "teacher.surname", target = "teacherSurname")
	CourseScheduleDtoResponse toDto(Schedule schedule);
}
