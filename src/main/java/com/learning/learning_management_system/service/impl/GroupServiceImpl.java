package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoResponse;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.exception.DataValidateException;
import com.learning.learning_management_system.mapper.GroupMapper;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.service.GroupService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(readOnly = true)
	public GroupDtoResponse getGroup(Long id) {
		Group group = groupRepository.findByIdOrThrow(id);
		log.info("Group with id = {} has been found", id);
		return groupMapper.toFullDto(group);
	}

	@Override
	@Transactional
	public GroupDtoResponse addGroup(GroupDtoGroupName groupDtoGroupName) {
		Group group = groupMapper.toEntityWithGroupName(groupDtoGroupName);
		if (groupRepository.findByGroupName(groupDtoGroupName.groupName()).isPresent()) {
			throw new DataValidateException("Group with name " + groupDtoGroupName.groupName() + " already exists");
		}
		groupRepository.save(group);
		log.info("Group has been added");

		return groupMapper.toDto(group);
	}

	@Override
	@Transactional
	public GroupDtoResponse updateGroup(Long id, GroupDtoGroupName groupDtoGroupName) {
		Group group = groupRepository.findByIdOrThrow(id);

		if (groupRepository.findByGroupName(groupDtoGroupName.groupName()).isPresent()) {
			throw new DataValidateException("Group with name " + groupDtoGroupName.groupName() + " already exists");
		}

		group.setGroupName(groupDtoGroupName.groupName());
		log.info("Group has been updated");

		return groupMapper.toFullDto(group);
	}

	@Override
	@Transactional
	public GroupDtoResponse deleteGroup(Long id) {
		Group group = groupRepository.findByIdOrThrow(id);

		Set<Student> students = group.getStudents();
		if (!students.isEmpty()) {
			throw new DataValidateException("Deletion is not possible. You must disband the group first");
		}
		group.setDeleted(true);
		log.info("Group has been deleted");
		return groupMapper.toFullDto(group);
	}

	@Override
	@Transactional
	public GroupDtoResponse addStudentToGroup(Long groupId, GroupDtoSetStudents studentsDto) {
		Group group = groupRepository.findByIdOrThrow(groupId);

		Set<Student> newStudents = studentsDto.studentsIds().stream()
					.map(studentRepository::findByIdOrThrow)
					.collect(Collectors.toSet());

		newStudents.forEach(student -> groupRepository.addStudentToGroup(groupId, student.getId()));

		log.info("Student has been added to group with id = {}", groupId);
		return groupMapper.toFullDto(group);
	}
}
