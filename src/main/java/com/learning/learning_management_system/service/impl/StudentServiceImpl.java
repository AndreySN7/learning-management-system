package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.StudentDto;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.mapper.StudentMapper;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.service.StudentService;
import com.learning.learning_management_system.validation.EntityValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
	private final StudentMapper studentMapper;
	private final StudentRepository studentRepository;
	private final EntityValidator entityValidator;

	@Override
	public StudentDto getStudent(Long id) {
		Student student = entityValidator.validateStudentExist(id);

		Set<String> studentGroups = student.getGroups().stream()
					.map(Group::getGroupName)
					.collect(Collectors.toSet());

		StudentDto studentDto = studentMapper.toDto(student);
		studentDto.groups().addAll(studentGroups);

		log.info("Student with id = {} has been found", id);
		return studentDto;
	}

	@Override
	@Transactional
	public void addStudent(StudentDto studentDto) {
		Student student = studentMapper.toEntity(studentDto);

		Set<Group> studentGroups = convertNamesToGroups(studentDto);
		student.setGroups(studentGroups);

		studentRepository.save(student);
		log.info("Student has been added");
	}

	@Override
	@Transactional
	public void updateStudent(Long id, StudentDto studentDto) {
		entityValidator.validateStudentExist(id);

		Set<Group> studentGroups = convertNamesToGroups(studentDto);
		Student student = studentMapper.toEntity(studentDto);
		student.setId(id);
		student.setGroups(studentGroups);

		studentRepository.save(student);
		log.info("Student has been updated");
	}

	@Override
	public void deleteStudent(Long id) {
		Student student = entityValidator.validateStudentExist(id);

		studentRepository.delete(student);
		log.info("Student has been deleted");
	}

	private @NonNull Set<Group> convertNamesToGroups(StudentDto studentDto) {
		return studentDto.groups().stream()
					.map(entityValidator::validateGroupNameExist)
					.collect(Collectors.toSet());
	}
}
