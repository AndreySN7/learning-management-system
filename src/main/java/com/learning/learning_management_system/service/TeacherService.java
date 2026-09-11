package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.teacher.TeacherDto;
import com.learning.learning_management_system.dto.teacher.TeacherDtoResponse;

public interface TeacherService {
	TeacherDtoResponse getTeacher(Long id);

	TeacherDtoResponse addTeacher(TeacherDto teacherDto);

	TeacherDtoResponse updateTeacher(Long id, TeacherDto teacherDto);

	TeacherDtoResponse deleteTeacher(Long id);
}
