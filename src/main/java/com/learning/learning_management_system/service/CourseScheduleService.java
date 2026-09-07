package com.learning.learning_management_system.service;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoCourseTime;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoGroupToCourse;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseScheduleService {
	void addGroupToCourse(Long groupId, CourseScheduleDtoGroupToCourse dto);

	void updateCourseTimeForGroup(Long id, CourseScheduleDtoCourseTime dto);

	Page<CourseScheduleDtoResponse> getScheduleForCourseByGroup(Long groupId, Pageable pageable);

	Page<CourseScheduleDtoResponse> getScheduleClassesByTeacher(Long teacherId, Pageable pageable);

	void removeCourseSchedule();
}