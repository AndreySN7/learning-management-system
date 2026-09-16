package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	default Teacher findByIdOrThrow(Long teacherId) {
		return findById(teacherId)
					.orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
	}
}