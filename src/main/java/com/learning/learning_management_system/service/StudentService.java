package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.StudentDto;

public interface StudentService {
	StudentDto getStudent(Long id);

	void addStudent(StudentDto studentDto);

	void updateStudent(Long id, StudentDto studentDto);

	void deleteStudent(Long id);
}
