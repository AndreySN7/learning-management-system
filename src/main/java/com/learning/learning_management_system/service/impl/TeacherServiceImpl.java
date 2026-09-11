package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.teacher.TeacherDto;
import com.learning.learning_management_system.dto.teacher.TeacherDtoResponse;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.mapper.TeacherMapper;
import com.learning.learning_management_system.repository.TeacherRepository;
import com.learning.learning_management_system.service.TeacherService;
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

	@Override
	public TeacherDtoResponse getTeacher(Long id) {
		Teacher teacher = teacherRepository.findByIdOrThrow(id);
		log.info("Teacher with id = {} has been found: {}", id, teacher);
		return teacherMapper.toDto(teacher);
	}

	@Override
	public TeacherDtoResponse addTeacher(TeacherDto teacherDto) {
		Teacher teacher = teacherMapper.toEntity(teacherDto);
		teacherRepository.save(teacher);
		log.info("Teacher has been added");
		return teacherMapper.toDto(teacher);
	}

	@Override
	@Transactional
	public TeacherDtoResponse updateTeacher(Long id, TeacherDto teacherDto) {
		Teacher currentTeacher = teacherRepository.findByIdOrThrow(id);
		currentTeacher.setName(teacherDto.name());
		currentTeacher.setSurname(teacherDto.surname());
		log.info("Teacher has been updated");
		return teacherMapper.toDto(currentTeacher);
	}

	@Override
	@Transactional
	public TeacherDtoResponse deleteTeacher(Long id) {
		Teacher teacher = teacherRepository.findByIdOrThrow(id);

		teacher.setDeleted(true);
		log.info("Teacher has been deleted");
		return teacherMapper.toDto(teacher);
	}
}