package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.student.StudentDto;
import com.learning.learning_management_system.dto.student.StudentDtoResponse;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.mapper.StudentMapper;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.service.StudentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {
	private final StudentMapper studentMapper;
	private final StudentRepository studentRepository;
	private final GroupRepository groupRepository;

	@Override
	@Transactional(readOnly = true)
	public StudentDtoResponse getStudent(Long id) {
		Student student = studentRepository.findByIdOrThrow(id);
		log.info("Student with id = {} has been found", id);
		return studentMapper.toFullDto(student);
	}

	@Override
	@Transactional
	public StudentDtoResponse addStudent(StudentDto studentDto) {
		Student student = studentMapper.toEntity(studentDto);

		Set<Group> studentGroups = convertNamesToGroups(studentDto);
		student.setGroups(studentGroups);
		studentRepository.save(student);

		log.info("Student has been added");
		return studentMapper.toFullDto(student);
	}

	@Override
	@Transactional
	public StudentDtoResponse updateStudent(Long id, StudentDto studentDto) {
		Set<Group> studentGroups = convertNamesToGroups(studentDto);

		Student student = studentRepository.findByIdOrThrow(id);
		student.setName(studentDto.name());
		student.setSurname(studentDto.surname());
		student.setGroups(studentGroups);
		log.info("Student has been updated");

		return studentMapper.toFullDto(student);
	}

	@Override
	@Transactional
	public StudentDtoResponse deleteStudent(Long id) {
		Student student = studentRepository.findByIdOrThrow(id);
		student.setDeleted(true);
		log.info("Student has been deleted");
		return studentMapper.toFullDto(student);
	}

	private @NonNull Set<Group> convertNamesToGroups(StudentDto studentDto) {
		return studentDto.groups().stream()
					.map(groupRepository::findByGroupNameOrThrow)
					.collect(Collectors.toSet());
	}
}
