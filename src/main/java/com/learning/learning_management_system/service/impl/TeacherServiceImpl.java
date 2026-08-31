package com.learning.learning_management_system.service.impl;

import com.learning.learning_management_system.dto.TeacherDto;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.exception.EntityNotFoundException;
import com.learning.learning_management_system.mapper.TeacherMapper;
import com.learning.learning_management_system.repository.TeacherRepository;
import com.learning.learning_management_system.service.TeacherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService {
	private final TeacherRepository teacherRepository;
	private final TeacherMapper teacherMapper;

	@Override
	public TeacherDto getTeacher(Long id) {
		Teacher teacher = teacherRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

		log.info("Teacher has been found");
		return teacherMapper.toDto(teacher);
	}

	@Override
	public void addTeacher(TeacherDto teacherDto) {
		Teacher teacher = teacherMapper.toEntity(teacherDto);
		teacherRepository.save(teacher);
		log.info("Teacher has been added");
	}

	@Override
	public void updateTeacher(Long id, TeacherDto teacherDto) {
		teacherRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

		Teacher currentTeacher = new Teacher(id, teacherDto.name(), teacherDto.surname());
		teacherRepository.save(currentTeacher);
		log.info("Teacher has been updated");
	}

	@Override
	public void deleteTeacher(Long id) {
		Teacher teacher = teacherRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

		teacherRepository.delete(teacher);
		log.info("Teacher has been deleted");
	}

}
