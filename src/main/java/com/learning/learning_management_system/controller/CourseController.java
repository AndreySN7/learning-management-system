package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.CourseDto;
import com.learning.learning_management_system.service.CourseService;
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

@RestController()
@RequestMapping(path = "/api/v1/course")
@AllArgsConstructor
public class CourseController {
	private final CourseService courseService;

	@GetMapping("/{id}")
	public CourseDto getCourse(@PathVariable(name = "id") Long id) {
		return courseService.getCourse(id);
	}

	@PostMapping
	public ResponseEntity<Void> addCourse(@Valid @RequestBody CourseDto courseDto) {
		courseService.addCourse(courseDto);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateCourse(@PathVariable(name = "id") Long id,
	                                         @Valid @RequestBody CourseDto courseDto) {
		courseService.updateCourse(id, courseDto);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCourse(@PathVariable(name = "id") Long id) {
		courseService.deleteCourse(id);
		return ResponseEntity.status((HttpStatus.OK)).build();
	}
}
