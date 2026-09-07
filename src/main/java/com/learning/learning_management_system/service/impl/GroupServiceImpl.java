package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.mapper.GroupMapper;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.service.GroupService;
import com.learning.learning_management_system.validation.EntityValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
	private final GroupRepository groupRepository;
	private final GroupMapper groupMapper;
	private final EntityValidator entityValidator;

	@Override
	public GroupDtoResponse getGroup(Long id) {
		Group group = entityValidator.validateGroupExist(id);

		Set<String> students = group.getStudents().stream()
					.map(student -> student.getName() + " " + student.getSurname())
					.collect(Collectors.toSet());

		GroupDtoResponse dtoResponse = groupMapper.toDto(group);
		dtoResponse.students().addAll(students);

		log.info("Group with id = {} has been found", id);
		return dtoResponse;
	}

	@Override
	@Transactional
	public void addGroup(GroupDtoGroupName groupDtoGroupName) {
		Group group = groupMapper.toEntityWithGroupName(groupDtoGroupName);
		entityValidator.validateGroupNameExist(groupDtoGroupName.groupName());

		groupRepository.save(group);
		log.info("Group has been added");
	}

	@Override
	@Transactional
	public void updateGroup(Long id, GroupDtoGroupName groupDtoGroupName) {
		Group group = entityValidator.validateGroupExist(id);
		entityValidator.validateGroupNameExist(groupDtoGroupName.groupName());


		group.setGroupName(groupDtoGroupName.groupName());
		groupRepository.save(group);
		log.info("Group has been updated");
	}

	@Override
	@Transactional
	public void deleteGroup(Long id) {
		Group group = entityValidator.validateGroupExist(id);

		Set<Student> students = group.getStudents();
		if (!students.isEmpty()) {
			throw new DataValidateException("Deletion is not possible. You must disband the group first");
		}

		groupRepository.delete(group);
		log.info("Group has been deleted");
	}

	@Override
	@Transactional
	public void addStudentToGroup(Long groupId, GroupDtoSetStudents studentsDto) {
		entityValidator.validateGroupExist(groupId);

		Set<Student> newStudents = studentsDto.studentsIds().stream()
					.map(entityValidator::validateStudentExist)
					.collect(Collectors.toSet());

		newStudents.forEach(student -> groupRepository.addStudentToGroup(groupId, student.getId()));

		log.info("Student has been added to group with id = {}", groupId);
	}
}
