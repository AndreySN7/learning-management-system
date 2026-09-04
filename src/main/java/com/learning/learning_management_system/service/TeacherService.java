package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.TeacherDto;

public interface TeacherService {
	TeacherDto getTeacher(Long id);

	void addTeacher(TeacherDto teacherDto);

	void updateTeacher(Long id, TeacherDto teacherDto);

	void deleteTeacher(Long id);
}
