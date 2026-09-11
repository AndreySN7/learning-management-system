package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.student.StudentDto;
import com.learning.learning_management_system.dto.student.StudentDtoResponse;
import com.learning.learning_management_system.service.StudentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/student")
@AllArgsConstructor
public class StudentController {
	private final StudentService studentService;

	@GetMapping("/{id}")
	public ResponseEntity<StudentDtoResponse> getStudent(@PathVariable(name = "id") Long id) {
		StudentDtoResponse dto = studentService.getStudent(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@PostMapping
	public ResponseEntity<StudentDtoResponse> addStudent(@Valid@RequestBody StudentDto studentDto) {
		StudentDtoResponse dto = studentService.addStudent(studentDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StudentDtoResponse> updateStudent (@PathVariable(name = "id") Long id,
	                                           @Valid @RequestBody StudentDto studentDto) {
		StudentDtoResponse dto = studentService.updateStudent(id, studentDto);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<StudentDtoResponse> deleteStudent(@PathVariable(name = "id") Long id) {
		StudentDtoResponse dto = studentService.deleteStudent(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
}
