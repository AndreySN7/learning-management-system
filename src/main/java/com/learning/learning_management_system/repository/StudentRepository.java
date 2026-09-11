package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

	default Student findByIdOrThrow(Long id) {
		return findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Student not found"));
	}
}