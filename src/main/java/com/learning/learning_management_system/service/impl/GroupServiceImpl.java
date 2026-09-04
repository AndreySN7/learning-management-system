package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import com.learning.learning_management_system.mapper.GroupMapper;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class GroupServiceImpl implements GroupService {
	private final GroupRepository groupRepository;
	private final GroupMapper groupMapper;
	private final StudentRepository studentRepository;

	@Override
	public GroupDtoResponse getGroup(Long id) {
		Group group = validGroup(id);

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
		validExistGroupName(groupDtoGroupName);

		groupRepository.save(group);
		log.info("Group has been added");
	}

	@Override
	@Transactional
	public void updateGroup(Long id, GroupDtoGroupName groupDtoGroupName) {
		Group group = validGroup(id);
		validExistGroupName(groupDtoGroupName);

		group.setGroupName(groupDtoGroupName.groupName());
		groupRepository.save(group);
		log.info("Group has been updated");
	}

	@Override
	@Transactional
	public void deleteGroup(Long id) {
		Group group = validGroup(id);

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
		validGroup(groupId);

		Set<Student> newStudents = studentsDto.studentsIds().stream()
					.map(id -> studentRepository.findById(id)
								.orElseThrow(() -> new EntityNotFoundException("Student not found")))
					.collect(Collectors.toSet());

		newStudents.forEach(student -> groupRepository.addStudentToGroup(groupId,student.getId()));

		log.info("Student has been added to group with id = {}", groupId);
	}

	private @NonNull Group validGroup(Long id) {
		return groupRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Group not found"));
	}

	private void validExistGroupName(GroupDtoGroupName groupDtoGroupName) {
		boolean isGroupNameExist = groupRepository.findByGroupName(groupDtoGroupName.groupName()).isPresent();
		if (isGroupNameExist) {
			throw new DataValidateException("Group with name " + groupDtoGroupName.groupName() + " already exists");
		}
	}
}
