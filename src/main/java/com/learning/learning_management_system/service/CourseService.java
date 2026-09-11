package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.course.CourseDto;
import com.learning.learning_management_system.dto.course.CourseDtoResponse;

public interface CourseService {
	CourseDtoResponse getCourse(Long id);

	CourseDtoResponse addCourse(CourseDto courseDto);

	CourseDtoResponse updateCourse(Long id, CourseDto courseDto);

	CourseDtoResponse deleteCourse(Long id);
}
