package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.TeacherDto;
import com.learning.learning_management_system.service.TeacherService;
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
@RequestMapping(path = "/api/v1/teacher")
@AllArgsConstructor
public class TeacherController {
	private final TeacherService teacherService;

	@GetMapping(path = "/{id}")
	public TeacherDto getTeacher(@PathVariable(name = "id") Long id) {
		return teacherService.getTeacher(id);
	}

	@PostMapping()
	public ResponseEntity<Void> addTeacher(@Valid @RequestBody TeacherDto teacherDto) {
		teacherService.addTeacher(teacherDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<Void> updateTeacher(@PathVariable(name = "id") Long id,
	                                          @Valid @RequestBody TeacherDto teacherDto) {
		teacherService.updateTeacher(id, teacherDto);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<Void> deleteTeacher(@PathVariable(name = "id") Long id) {
		teacherService.deleteTeacher(id);
		return ResponseEntity.ok().build();
	}
}
