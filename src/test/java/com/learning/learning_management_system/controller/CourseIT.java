package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.course.CourseDto;
import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.repository.CourseRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CourseIT extends AbstractIntegrationTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	ObjectMapper objectMapper;

	private Course course;

	@BeforeEach
	void setup() {
		course = new Course(null, "Course1", "Description", false);
		courseRepository.save(course);
	}

	@AfterEach
	void cleanAfterTest() {
		courseRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден -> возвращаем курс")
	void getCourse_whenIdFound_thenReturnCourse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course/{id}", course.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(course.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Course1"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.description")
								.value("Description"));

		Optional<Course> courseOpt = courseRepository.findById(course.getId());
		assertThat(courseOpt).isPresent();
		assertThat(courseOpt.get().isDeleted()).isFalse();
	}

	@Test
	@DisplayName("id не найден -> возвращаем EntityNotFoundException(\"Course not found\")")
	void getCourse_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course/{id}", 111111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Course not found"));
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void getCourse_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Description"})
	@DisplayName("name не пустой, description любой -> курс добавлен, возвращаем дто")
	void addCourse_whenNameNotEmpty_thenAddCourse(String description) throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(new CourseDto("C01", description))))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("C01"));

		Course savedCourse = courseRepository.findAll().stream()
					.filter(c -> c.getName().equals("C01"))
					.findFirst()
					.orElseThrow();
		assertThat(savedCourse.getDescription()).isEqualTo(description);
		assertThat(savedCourse.isDeleted()).isFalse();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("name null or empty -> Value can not be empty")
	void addCourse_whenNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		CourseDto request = new CourseDto(name, "");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Descr2"})
	@DisplayName("id найден, name не пустой, description любой -> курс обновлен, возвращаем дто")
	void updateCourse_whenIdFoundNameNotEmpty_thenCourseUpdate(String description) throws Exception {
		CourseDto request = new CourseDto("K01", description);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course/{id}", course.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(course.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(request.name()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(request.description()));

		Optional<Course> courseOpt = courseRepository.findById(course.getId());
		assertThat(courseOpt).isPresent();
		assertThat(courseOpt.get().getName()).isEqualTo(request.name());
		assertThat(courseOpt.get().getDescription()).isEqualTo(request.description());
		assertThat(courseOpt.get().isDeleted()).isFalse();
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("id найден, name пустой -> Value can not be empty")
	void updateCourse_whenIdFoundNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		CourseDto request = new CourseDto(name, "");
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course/{id}", course.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@Test
	@DisplayName("id не найден -> Course not found")
	void updateCourse_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		CourseDto request = new CourseDto("S01", "Desc");
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Course not found"));
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void updateCourse_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course/"))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id найден -> soft delete")
	void deleteCourse_whenIdFound_thenSoftDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/course/{id}", course.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(course.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(course.getName()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(course.getDescription()));

		List<Course> courseOpt = courseRepository.findAll();
		assertThat(courseOpt).isEmpty();
		assertThat(courseRepository.isDeletedById(course.getId())).isTrue();
	}

	@Test
	@DisplayName("id null -> внутренняя ошибка сервера")
	void deleteCourse_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/course/"))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> Course not found")
	void deleteCourse_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/course/{id}", 1111L))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Course not found"));
	}
}