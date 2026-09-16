package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

	default Course findByIdOrThrow(Long id) {
		return findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Course not found"));
	}

	@Query(value = "select c.deleted from course c where id = :id", nativeQuery = true)
	Boolean isDeletedById(@Param(value = "id") Long id);
}