package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

	@EntityGraph(attributePaths = "groups")
	@Query(value = "select s from Student s where s.id = :id")
	Optional<Student> findByIdWithGroups(@Param("id") Long id);

	@Query(value = "select s.deleted from student s where id = :id", nativeQuery = true)
	Boolean isDeletedById(@Param(value = "id") Long id);

	default Student findByIdOrThrow(Long id) {
		return findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Student not found"));
	}
}