package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface CourseScheduleRepository extends JpaRepository<Schedule, Long> {
	@EntityGraph(attributePaths = {"group", "course", "teacher"})
	Page<Schedule> findAllByGroupId(Long groupId, Pageable pageable);

	@EntityGraph(attributePaths = {"group", "teacher", "course"})
	Page<Schedule> findAllByTeacherId(Long teacherId, Pageable pageable);

	List<Schedule> findAllByEndDateBefore(LocalDate localDate);
}