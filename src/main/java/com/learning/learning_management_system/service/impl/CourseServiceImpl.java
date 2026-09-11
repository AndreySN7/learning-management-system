package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.course.CourseDto;
import com.learning.learning_management_system.dto.course.CourseDtoResponse;
import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.mapper.CourseMapper;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.service.CourseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
	private final CourseRepository courseRepository;
	private final CourseMapper courseMapper;

	@Override
	public CourseDtoResponse getCourse(Long id) {
		Course course = courseRepository.findByIdOrThrow(id);

		log.info("Course with id = {} has been found: {} ", id, course);
		return courseMapper.toDto(course);
	}

	@Override
	public CourseDtoResponse addCourse(CourseDto courseDto) {
		Course course = courseMapper.toEntity(courseDto);
		courseRepository.save(course);
		log.info("Course has been added");
		return courseMapper.toDto(course);
	}

	@Override
	@Transactional
	public CourseDtoResponse updateCourse(Long id, CourseDto courseDto) {
		Course course = courseRepository.findByIdOrThrow(id);
		course.setName(courseDto.name());
		course.setDescription(courseDto.description());
		log.info("Course has been updated");
		return courseMapper.toDto(course);
	}

	@Override
	@Transactional
	public CourseDtoResponse deleteCourse(Long id) {
		Course course = courseRepository.findByIdOrThrow(id);
		course.setDeleted(true);
		log.info("Course has been deleted");
		return courseMapper.toDto(course);
	}
}
