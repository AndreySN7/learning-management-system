package com.learning.learning_management_system.validation;

import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Schedule;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntityValidator {
	private final GroupRepository groupRepository;
	private final CourseRepository courseRepository;
	private final TeacherRepository teacherRepository;
	private final CourseScheduleRepository courseScheduleRepository;
	private final StudentRepository studentRepository;

	public @NonNull Student validateStudentExist(Long id) {
		return studentRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Student not found"));
	}

	public @NonNull Group validateGroupExist(Long groupId) {
		return groupRepository.findById(groupId)
					.orElseThrow(() -> new EntityNotFoundException("Group not found"));
	}

	public @NonNull Teacher validateTeacherExist(Long teacherId) {
		return teacherRepository.findById(teacherId)
					.orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
	}

	public @NonNull Course validateCourseExist(Long id) {
		return courseRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));
	}

	public @NonNull Schedule validateScheduleExist(Long scheduleId) {
		return courseScheduleRepository.findById(scheduleId)
					.orElseThrow(() -> new EntityNotFoundException("CourseSchedule not found"));
	}

	public @NonNull Group validateGroupNameExist(String groupName) {
		return groupRepository.findByGroupName(groupName)
					.orElseThrow(() -> new EntityNotFoundException("Group " + groupName + " not found"));
	}

	public void validateScheduleExist(Page<Schedule> courseSchedulePage) {
		if (courseSchedulePage.getTotalElements() == 0) {
			throw new EntityNotFoundException("Course schedule not found");
		}
	}
}
