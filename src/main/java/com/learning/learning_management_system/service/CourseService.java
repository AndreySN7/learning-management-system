package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.CourseDto;

public interface CourseService {
	CourseDto getCourse(Long id);

	void addCourse(CourseDto courseDto);

	void updateCourse(Long id, CourseDto courseDto);

	void deleteCourse(Long id);
}
