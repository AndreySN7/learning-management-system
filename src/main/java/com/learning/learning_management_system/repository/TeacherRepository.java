package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	default Teacher findByIdOrThrow(Long teacherId) {
		return findById(teacherId)
					.orElseThrow(() -> new EntityNotFoundException("Teacher not found"));
	}

	@Query(value = "select t.deleted from teacher t where id = :id", nativeQuery = true)
	Boolean isDeletedById(@Param(value = "id") Long id);
}