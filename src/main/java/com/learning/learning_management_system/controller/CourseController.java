package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.course.CourseDto;
import com.learning.learning_management_system.dto.course.CourseDtoResponse;
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
	public ResponseEntity<CourseDtoResponse> getCourse(@PathVariable(name = "id") Long id) {
		CourseDtoResponse dto = courseService.getCourse(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@PostMapping
	public ResponseEntity<CourseDtoResponse> addCourse(@Valid @RequestBody CourseDto courseDto) {
		CourseDtoResponse dto = courseService.addCourse(courseDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CourseDtoResponse> updateCourse(@PathVariable(name = "id") Long id,
	                                                      @Valid @RequestBody CourseDto courseDto) {
		CourseDtoResponse dto = courseService.updateCourse(id, courseDto);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<CourseDtoResponse> deleteCourse(@PathVariable(name = "id") Long id) {
		CourseDtoResponse dto = courseService.deleteCourse(id);
		return ResponseEntity.status(HttpStatus.OK).body(dto);
	}
}
