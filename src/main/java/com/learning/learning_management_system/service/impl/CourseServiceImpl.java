package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.CourseDto;
import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import com.learning.learning_management_system.mapper.CourseMapper;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
	private final CourseRepository courseRepository;
	private final CourseMapper courseMapper;

	@Override
	public CourseDto getCourse(Long id) {
		Course course = validCourse(id);

		log.info("Course with id = {} has been found: {} ", id, course);
		return courseMapper.toDto(course);
	}

	@Override
	public void addCourse(CourseDto courseDto) {
		Course course = courseMapper.toEntity(courseDto);
		courseRepository.save(course);
		log.info("Course has been added");
	}

	@Override
	@Transactional
	public void updateCourse(Long id, CourseDto courseDto) {
		validCourse(id);

		Course course = new Course(id, courseDto.name(), courseDto.description());
		courseRepository.save(course);
		log.info("Course has been updated");
	}

	@Override
	@Transactional
	public void deleteCourse(Long id) {
		Course course = validCourse(id);
		courseRepository.delete(course);
		log.info("Course has been deleted");
	}

	private @NonNull Course validCourse(Long id) {
		return courseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));
	}
}
