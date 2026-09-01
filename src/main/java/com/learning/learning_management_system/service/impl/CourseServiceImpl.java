package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.CourseDto;
import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import com.learning.learning_management_system.mapper.CourseMapper;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
	private final CourseRepository courseRepository;
	private final CourseMapper courseMapper;

	@Override
	public CourseDto getCourse(Long id) {
		Course course = courseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));

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
	public void updateCourse(Long id, CourseDto courseDto) {
		courseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));

		Course course = new Course(id, courseDto.name(), courseDto.description());
		courseRepository.save(course);
		log.info("Course has been updated");
	}

	@Override
	public void deleteCourse(Long id) {
		Course course = courseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));
		courseRepository.delete(course);
		log.info("Course has been deleted");
	}
}
