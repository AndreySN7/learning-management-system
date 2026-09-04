package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.StudentDto;
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
	public StudentDto getStudent(@PathVariable(name = "id") Long id) {
		return studentService.getStudent(id);
	}

	@PostMapping
	public ResponseEntity<Void> addStudent(@RequestBody StudentDto studentDto) {
		studentService.addStudent(studentDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateStudent (@PathVariable(name = "id") Long id,
	                                           @Valid @RequestBody StudentDto studentDto) {
		studentService.updateStudent(id, studentDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteStudent(@PathVariable(name = "id") Long id) {
		studentService.deleteStudent(id);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
