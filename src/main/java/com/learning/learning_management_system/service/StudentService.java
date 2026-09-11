package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.student.StudentDto;
import com.learning.learning_management_system.dto.student.StudentDtoResponse;

public interface StudentService {
	StudentDtoResponse getStudent(Long id);

	StudentDtoResponse addStudent(StudentDto studentDto);

	StudentDtoResponse updateStudent(Long id, StudentDto studentDto);

	StudentDtoResponse deleteStudent(Long id);
}
