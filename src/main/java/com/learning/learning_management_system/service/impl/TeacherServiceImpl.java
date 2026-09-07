package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.TeacherDto;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.mapper.TeacherMapper;
import com.learning.learning_management_system.repository.TeacherRepository;
import com.learning.learning_management_system.service.TeacherService;
import com.learning.learning_management_system.validation.EntityValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService {
	private final TeacherRepository teacherRepository;
	private final TeacherMapper teacherMapper;
	private final EntityValidator entityValidator;

	@Override
	public TeacherDto getTeacher(Long id) {
		Teacher teacher = entityValidator.validateTeacherExist(id);

		log.info("Teacher with id = {} has been found: {}", id, teacher);
		return teacherMapper.toDto(teacher);
	}

	@Override
	public void addTeacher(TeacherDto teacherDto) {
		Teacher teacher = teacherMapper.toEntity(teacherDto);
		teacherRepository.save(teacher);
		log.info("Teacher has been added");
	}

	@Override
	@Transactional
	public void updateTeacher(Long id, TeacherDto teacherDto) {
		entityValidator.validateTeacherExist(id);

		Teacher currentTeacher = new Teacher(id, teacherDto.name(), teacherDto.surname());
		teacherRepository.save(currentTeacher);
		log.info("Teacher has been updated");
	}

	@Override
	@Transactional
	public void deleteTeacher(Long id) {
		Teacher teacher = entityValidator.validateTeacherExist(id);

		teacherRepository.delete(teacher);
		log.info("Teacher has been deleted");
	}
}
