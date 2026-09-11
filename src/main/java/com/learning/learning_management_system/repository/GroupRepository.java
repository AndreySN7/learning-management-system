package com.learning.learning_management_system.repository;

import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
	Optional<Group> findByGroupName(String groupName);

	@Modifying
	@Query(value = """
				INSERT INTO students_groups(group_id, student_id)
				VALUES(:group_id, :student_id)
				ON CONFLICT (group_id, student_id) DO NOTHING""",
				nativeQuery = true)
	void addStudentToGroup(@Param("group_id") Long groupId, @Param("student_id") Long studentId);

	default Group findByIdOrThrow(Long groupId) {
		return findById(groupId)
					.orElseThrow(() -> new EntityNotFoundException("Group not found"));
	}

	default Group findByGroupNameOrThrow(String groupName) {
		return findByGroupName(groupName)
					.orElseThrow(() -> new EntityNotFoundException("Group " + groupName + " not found"));
	}
}