package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoCourseTime;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoGroupToCourse;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoResponse;
import com.learning.learning_management_system.service.CourseScheduleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/course-schedule")
@Slf4j
public class CourseScheduleController {
	private final CourseScheduleService courseScheduleService;

	@PostMapping("group/{id_group}")
	public ResponseEntity<Void> addGroupToCourse(@PathVariable(name = "id_group") Long groupId,
	                                             @RequestBody CourseScheduleDtoGroupToCourse dto) {
		courseScheduleService.addGroupToCourse(groupId, dto);
		log.info("DTO: teacherId={}, coursesIds={}", dto.teacherId(), dto.coursesIds());
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> updateCourseTimeForGroup(@PathVariable(name = "id") Long id,
	                                                     @Valid @RequestBody CourseScheduleDtoCourseTime dto) {
		courseScheduleService.updateCourseTimeForGroup(id, dto);
		return ResponseEntity.status(HttpStatus.OK).build();

	}

	@GetMapping("/group/{id_group}")
	public Page<CourseScheduleDtoResponse> getScheduleCourseForGroup(
				@PathVariable(name = "id_group") Long groupId,
	      @PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable  pageable) {
		return courseScheduleService.getScheduleForCourseByGroup(groupId, pageable);
	}

	@GetMapping("/teacher/{id_teacher}")
	public Page<CourseScheduleDtoResponse> getScheduleClassesForTeacher(
				@PathVariable(name = "id_teacher") Long teacherId,
				@PageableDefault(page = 0, size = 10, direction = Sort.Direction.ASC) Pageable pageable) {
		return courseScheduleService.getScheduleClassesByTeacher(teacherId, pageable);
	}
}
