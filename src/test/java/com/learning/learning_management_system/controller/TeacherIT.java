package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.teacher.TeacherDto;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherIT extends AbstractIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private ObjectMapper objectMapper;

	private Teacher teacher;

	@BeforeEach
	void setUp() {
		teacher = new Teacher(null, "TeacherName", "TeacherSurname", false);
		teacherRepository.save(teacher);
	}

	@AfterEach
	void cleanAfterTest() {
		teacherRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден -> возвращаем курс")
	void getTeacher_whenIdFound_thenReturnTeacher() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/{id}", teacher.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(teacher.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("TeacherName"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("TeacherSurname"));

		Optional<Teacher> teacherOpt = teacherRepository.findById(teacher.getId());
		assertThat(teacherOpt).isPresent();
		assertThat(teacherOpt.get().isDeleted()).isFalse();
	}

	@Test
	@DisplayName("id не найден -> Teacher not found")
	void getTeacher_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Teacher not found"));
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void getTeacher_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/teacher/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Family"})
	@DisplayName("name не пустой, surname любой -> учитель добавлен, возвращаем дто")
	void addTeacher_whenNameNotEmpty_thenAddTeacher(String surname) throws Exception {
		TeacherDto request = new TeacherDto("Pol", surname);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teacher")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Pol"));

		Optional<Teacher> teacherOpt = teacherRepository.findAll().stream()
					.filter(t -> t.getName().equals("Pol"))
					.findFirst();
		assertThat(teacherOpt).isPresent();
		assertThat(teacherOpt.get().getSurname()).isEqualTo(surname);
		assertThat(teacherOpt.get().isDeleted()).isFalse();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("name null or empty -> Value can not be empty")
	void addTeacher_whenNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		TeacherDto request = new TeacherDto(name, "Gram");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/teacher")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Millis"})
	@DisplayName("id найден, name не пустой, surname любой -> курс обновлен, возвращаем дто")
	void updateTeacher_whenIdFoundNameNotEmpty_thenTeacherUpdate(String surname) throws Exception {
		TeacherDto request = new TeacherDto("Ken", surname);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/teacher/{id}", teacher.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Ken"));

		Optional<Teacher> teacherOpt = teacherRepository.findById(teacher.getId());
		assertThat(teacherOpt).isPresent();
		assertThat(teacherOpt.get().getSurname()).isEqualTo(surname);
		assertThat(teacherOpt.get().isDeleted()).isFalse();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("id найден, name пустой -> Value can not be empty")
	void updateTeacher_whenIdFoundNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		TeacherDto request = new TeacherDto(name, "Fam");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/teacher/{id}", teacher.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));

	}

	@Test
	@DisplayName("id не найден -> Teacher not found")
	void updateTeacher_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/teacher/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(new TeacherDto("Kon", "Fem"))))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Teacher not found"));
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void updateTeacher_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/teacher/")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id найден -> soft delete")
	void deleteTeacher_whenIdFound_thenSoftDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/teacher/{id}", teacher.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(teacher.getName()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(teacher.getSurname()));

		Optional<Teacher> teacherOpt = teacherRepository.findById(teacher.getId());
		assertThat(teacherOpt).isEmpty();
		assertThat(teacherRepository.isDeletedById(teacher.getId())).isTrue();
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void deleteTeacher_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/teacher/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> Teacher not found")
	void deleteTeacher_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/teacher/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Teacher not found"));
	}
}
