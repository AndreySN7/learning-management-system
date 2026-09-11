package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoCourseTime;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoGroupToCourse;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoResponse;
import com.learning.learning_management_system.entity.Schedule;
import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.mapper.CourseScheduleMapper;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.TeacherRepository;
import com.learning.learning_management_system.service.CourseScheduleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CourseScheduleServiceImpl implements CourseScheduleService {
	private final CourseScheduleRepository courseScheduleRepository;
	private final CourseScheduleMapper courseScheduleMapper;
	private final GroupRepository groupRepository;
	private final TeacherRepository teacherRepository;
	private final CourseRepository courseRepository;

	@Override
	@Transactional
	public void addGroupToCourse(Long groupId, CourseScheduleDtoGroupToCourse dto) {
		groupRepository.findByIdOrThrow(groupId);
		if (dto.teacherId() != null) {
			teacherRepository.findByIdOrThrow(dto.teacherId());
		}

		Set<Schedule> courseSchedule = dto.coursesIds().stream()
					.map(id -> {
						courseRepository.findByIdOrThrow(id);
						return Schedule.builder()
									.groupId(groupId)
									.teacherId(dto.teacherId())
									.courseId(id)
									.build();
					})
					.collect(Collectors.toSet());

		courseScheduleRepository.saveAll(courseSchedule);
		log.info("Course schedule has been added");
	}

	@Override
	@Transactional
	public void updateCourseTimeForGroup(Long id, CourseScheduleDtoCourseTime dto) {
		Schedule courseSchedule = courseScheduleRepository.findByIdOrThrow(id);
		if (dto.endDate().isBefore(dto.startDate())) {
			throw new DataValidateException("The end date cannot be less than the start date");
		}
		courseSchedule.setStartDate(dto.startDate());
		courseSchedule.setEndDate(dto.endDate());

		log.info("Course schedule has been updated");
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CourseScheduleDtoResponse> getScheduleForCourseByGroup(Long groupId, Pageable pageable) {
		groupRepository.findByIdOrThrow(groupId);
		Page<Schedule> courseSchedulePageByGroup = courseScheduleRepository.findAllByGroupId(groupId, pageable);

		log.info("Course schedule by group has been found");
		return courseSchedulePageByGroup.map(courseScheduleMapper::toDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<CourseScheduleDtoResponse> getScheduleClassesByTeacher(Long teacherId, Pageable pageable) {
		teacherRepository.findByIdOrThrow(teacherId);
		Page<Schedule> courseSchedulePageByTeacher = courseScheduleRepository.findAllByTeacherId(teacherId, pageable);

		log.info("Course schedule by teacher has been found");
		return courseSchedulePageByTeacher.map(courseScheduleMapper::toDto);
	}
}
