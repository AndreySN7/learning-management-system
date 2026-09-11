package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.teacher.TeacherDto;
import com.learning.learning_management_system.dto.teacher.TeacherDtoResponse;
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
	public ResponseEntity<TeacherDtoResponse> getTeacher(@PathVariable(name = "id") Long id) {
		TeacherDtoResponse dto = teacherService.getTeacher(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@PostMapping()
	public ResponseEntity<TeacherDtoResponse> addTeacher(@Valid @RequestBody TeacherDto teacherDto) {
		TeacherDtoResponse dto = teacherService.addTeacher(teacherDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<TeacherDtoResponse> updateTeacher(@PathVariable(name = "id") Long id,
	                                          @Valid @RequestBody TeacherDto teacherDto) {
		TeacherDtoResponse dto = teacherService.updateTeacher(id, teacherDto);
		return ResponseEntity.ok().body(dto);
	}

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<TeacherDtoResponse> deleteTeacher(@PathVariable(name = "id") Long id) {
		TeacherDtoResponse dto = teacherService.deleteTeacher(id);
		return ResponseEntity.ok().body(dto);
	}
}
