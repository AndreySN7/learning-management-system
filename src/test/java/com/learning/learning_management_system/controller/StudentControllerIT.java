package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.student.StudentDto;
import com.learning.learning_management_system.dto.student.StudentDtoResponse;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Student;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StudentControllerIT extends AbstractIntegrationTest {
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private Student student;
	private Group group1, group2;

	@BeforeEach
	void setUp() {
		group1 = new Group(null, "Gr1", false, new HashSet<>());
		group2 = new Group(null, "Gr2", false, new HashSet<>());
		List<Group> savedGroups = List.of(group1, group2);
		groupRepository.saveAll(savedGroups);

		Set<Group> groups = new HashSet<>(savedGroups);
		student = new Student(null, "Student", "Family", false, groups);
		studentRepository.save(student);
	}

	@AfterEach
	void cleanAfterTest() {
		jdbcTemplate.update("update student set deleted = false");
		studentRepository.deleteAll();
		groupRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден -> возвращаем ученика")
	void getStudent_whenIdFound_thenReturnStudent() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/{id}", student.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(student.getName()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(student.getSurname()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.groups",
								Matchers.containsInAnyOrder("Gr1", "Gr2")));

		Optional<Student> studentOpt = studentRepository.findById(student.getId());
		assertThat(studentOpt).isPresent();
		assertThat(studentOpt.get().isDeleted()).isFalse();
	}

	@Test
	@DisplayName("id не найден -> Student not found")
	void getStudent_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Student not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void getStudent_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/student/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Fam"})
	@DisplayName("name непустое, surname любое, список групп не пуст -> добавляем ученика")
	void addStudent_whenNameAndGroupSetNotEmptyAndAnySurname_thenAddStudent(String surname) throws Exception {
		Group newGroup1 =
					groupRepository.save(new Group(null, "Gr01", false, new HashSet<>()));
		StudentDto request =
					new StudentDto("Tom", surname, Set.of(newGroup1.getGroupName(), group2.getGroupName()));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/student")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andReturn();

		StudentDtoResponse response =
					objectMapper.readValue(result.getResponse().getContentAsString(), StudentDtoResponse.class);

		Optional<Student> studentOpt = studentRepository.findByIdWithGroups(response.id());
		assertThat(studentOpt).isPresent();
		assertThat(studentOpt.get().isDeleted()).isFalse();
		assertThat(studentOpt.get().getSurname()).isEqualTo(surname);
		assertThat(studentOpt.get().getGroups()).extracting(Group::getGroupName)
					.containsExactlyInAnyOrder("Gr01", "Gr2");
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Tim"})
	@DisplayName("name непустое, surname любое, список групп пуст -> Group set cannot be empty")
	void addStudent_whenGroupSetEmpty_thenValidationBeingPerformed(String surname) throws Exception {
		StudentDto request = new StudentDto("Jack", surname, null);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/student")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group set cannot be empty"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("name пустое, surname любое, список групп не пуст -> Value can not be empty")
	void addStudent_whenNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		StudentDto request = new StudentDto(name, "Mitt", Set.of(group1.getGroupName(), group2.getGroupName()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/student")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@Test
	@DisplayName("name непустое, surname любое, группа из списка групп не существует -> Group 'groupName' not found")
	void addStudent_whenGroupSetInvalid_thenValidationBeingPerformed() throws Exception {
		StudentDto request = new StudentDto("Kate", "Killy", Set.of("InvalidGroup"));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/student")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group InvalidGroup not found"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "Mid"})
	@DisplayName("id найден, name непустое, surname любое, список групп не пуст -> обновляем ученика")
	void updateStudent_whenIdFoundNameAndGroupSetNotEmptyAndAnySurname_thenUpdateStudent(String surname) throws Exception {
		StudentDto request = new StudentDto("Alla", surname, Set.of(group1.getGroupName()));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", student.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.groups",
								Matchers.containsInAnyOrder("Gr1")));

		Optional<Student> savedStudent = studentRepository.findByIdWithGroups(student.getId());
		assertThat(savedStudent).isPresent();
		assertThat(savedStudent.get().getName()).isEqualTo("Alla");
		assertThat(savedStudent.get().getSurname()).isEqualTo(surname);
		assertThat(savedStudent.get().getGroups()).extracting(Group::getGroupName)
					.containsExactlyInAnyOrder("Gr1");
	}

	@Test
	@DisplayName("id найден, name непустое, surname непустое, список групп пуст -> Group set cannot be empty")
	void updateStudent_whenGroupSetEmpty_thenValidationBeingPerformed() throws Exception {
		StudentDto request = new StudentDto("Nik", "Niko", null);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", student.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group set cannot be empty"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("id найден, name пустое, surname любое, список групп не пуст -> Value can not be empty")
	void updateStudent_whenNameEmpty_thenValidationBeingPerformed(String name) throws Exception {
		StudentDto request = new StudentDto(name, "Gan", Set.of(group1.getGroupName()));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", student.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@Test
	@DisplayName("id найден, name непустое, surname любое, группа из списка групп не существует -> " +
				"Group 'groupName' not found")
	void updateStudent_whenGroupSetInvalid_thenValidationBeingPerformed() throws Exception {
		StudentDto request = new StudentDto("Noy", "Gan", Set.of("InvalidGroup"));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", student.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group InvalidGroup not found"));
	}

	@Test
	@DisplayName("id не найден -> Student not found")
	void updateStudent_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		StudentDto request = new StudentDto("Noy", "Gan", Set.of(group2.getGroupName()));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Student not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void updateStudent_whenIdNull_thenInternalServerError() throws Exception {
		StudentDto request = new StudentDto("Noy", "Gan", Set.of(group2.getGroupName()));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/student/")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id найден -> soft delete")
	void deleteStudent_whenIdFound_thenSoftDelete() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}", student.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(student.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Student"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.surname").value("Family"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.groups",
								Matchers.containsInAnyOrder("Gr1", "Gr2")));

		Optional<Student> studentOpt = studentRepository.findById(student.getId());
		assertThat(studentOpt).isEmpty();
		assertThat(studentRepository.isDeletedById(student.getId())).isTrue();
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void deleteStudent_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> Student not found")
	void deleteStudent_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/student/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Student not found"));
	}
}