package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoCourseTime;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoGroupToCourse;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoResponse;
import com.learning.learning_management_system.entity.Schedule;
import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.mapper.CourseScheduleMapper;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import com.learning.learning_management_system.service.CourseScheduleService;
import com.learning.learning_management_system.validation.EntityValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CourseScheduleServiceImpl implements CourseScheduleService {
	private final CourseScheduleRepository courseScheduleRepository;
	private final EntityValidator entityValidator;
	private final CourseScheduleMapper courseScheduleMapper;

	@Override
	@Transactional
	public void addGroupToCourse(Long groupId, CourseScheduleDtoGroupToCourse dto) {
		entityValidator.validateGroupExist(groupId);
		if (dto.teacherId() != null) {
			entityValidator.validateTeacherExist(dto.teacherId());
		}

		Set<Schedule> courseSchedule = dto.coursesIds().stream()
					.map(id -> {
						entityValidator.validateCourseExist(id);
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
		Schedule courseSchedule = entityValidator.validateScheduleExist(id);
		if (dto.endDate().isBefore(dto.startDate())) {
			throw new DataValidateException("The end date cannot be less than the start date");
		}
		courseSchedule.setStartDate(dto.startDate());
		courseSchedule.setEndDate(dto.endDate());

		courseScheduleRepository.save(courseSchedule);
		log.info("Course schedule has been updated");
	}

	@Override
	@Transactional
	public Page<CourseScheduleDtoResponse> getScheduleForCourseByGroup(Long groupId, Pageable pageable) {
		entityValidator.validateGroupExist(groupId);
		Page<Schedule> courseSchedulePageByGroup = courseScheduleRepository.findAllByGroupId(groupId, pageable);
		entityValidator.validateScheduleExist(courseSchedulePageByGroup);

		log.info("Course schedule by group has been found");
		return courseSchedulePageByGroup.map(courseScheduleMapper::toDto);
	}

	@Override
	@Transactional
	public Page<CourseScheduleDtoResponse> getScheduleClassesByTeacher(Long teacherId, Pageable pageable) {
		entityValidator.validateTeacherExist(teacherId);
		Page<Schedule> courseSchedulePageByTeacher = courseScheduleRepository.findAllByTeacherId(teacherId, pageable);
		entityValidator.validateScheduleExist(courseSchedulePageByTeacher);

		log.info("Course schedule by teacher has been found");
		return courseSchedulePageByTeacher.map(courseScheduleMapper::toDto);
	}

	@Override
	@Scheduled(cron = "${app.cleanup.cron}")
	@Transactional
	public void removeCourseSchedule() {
		LocalDate verificationDate = LocalDate.now().minusYears(1);
		List<Schedule> allByEndDateBefore = courseScheduleRepository.findAllByEndDateBefore(verificationDate);

		if (allByEndDateBefore.isEmpty()) {
			log.info("Course schedule not found to delete");
		} else {
			courseScheduleRepository.deleteAll(allByEndDateBefore);
			log.info("Course schedule has been removed ({} records)", allByEndDateBefore.size());
		}
	}
}
