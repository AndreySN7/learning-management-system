package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.group.GroupDtoGroupName;
import com.learning.learning_management_system.dto.group.GroupDtoSetStudents;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GroupControllerIT extends AbstractIntegrationTest {
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	private Group group;
	private Student student;

	@BeforeEach
	void setUp() {
		group = new Group(null, "Group1", false, new HashSet<>());
		groupRepository.save(group);
		student = new Student(null, "Name", "Surname", false, Set.of(group));
		studentRepository.save(student);
	}

	@AfterEach
	void cleanAfterTest() {
		studentRepository.deleteAll();
		groupRepository.deleteAll();
	}

	@Test
	@DisplayName("id найден -> возвращаем группу")
	void getGroup_whenIdFound_thenReturnGroup() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group/{id}", group.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(group.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.groupName").value("Group1"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.students",
								Matchers.containsInAnyOrder("Name Surname")));

		Optional<Group> groupOpt = groupRepository.findByIdWithStudents(group.getId());
		assertThat(groupOpt).isPresent();
		assertThat(groupOpt.get().getGroupName()).isEqualTo("Group1");
		assertThat(groupOpt.get().getStudents())
					.extracting(s -> s.getName() + " " + s.getSurname())
					.containsExactlyInAnyOrder("Name Surname");
		assertThat(groupOpt.get().isDeleted()).isFalse();
	}

	@Test
	@DisplayName("id не найден -> Group not found")
	void getGroup_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void getGroup_whenIdNotFound_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/group/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("groupName непустая и новая -> добавляем группу")
	void addGroup_whenGroupNameNotBlankAndNew_thenAddGroup() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("TR1");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.groupName").value("TR1"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.students").isEmpty());

		List<Group> groupList = groupRepository.findAll();
		assertThat(groupList).hasSize(2);
	}

	@Test
	@DisplayName("groupName непуста и уже существует -> Group with name 'groupName' already exists")
	void addGroup_whenGroupNameNotBlankAndExist_thenValidationBeingPerformed() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("Group1");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group with name " + request.groupName() + " already exists"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("groupName пустая -> Value can not be empty")
	void addGroup_whenGroupEmpty_thenValidationBeingPerformed(String groupName) throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName(groupName);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@Test
	@DisplayName("id найден, groupName непустая и не существует -> обновляем группу")
	void updateGroup_whenGroupNameNotBlankAndNew_thenUpdateGroup() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("Mp2");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.groupName").value("Mp2"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.students",
								Matchers.containsInAnyOrder("Name Surname")));

		List<Group> groupList = groupRepository.findAll();
		assertThat(groupList).hasSize(1);
		Optional<Group> groupOpt = groupRepository.findByIdWithStudents(group.getId());
		assertThat(groupOpt).isPresent();
		assertThat(groupOpt.get().getGroupName()).isEqualTo("Mp2");
		assertThat(groupOpt.get().getStudents())
					.hasSize(1)
					.extracting(s -> s.getName() + " " + s.getSurname())
					.containsExactlyInAnyOrder("Name Surname");
	}

	@Test
	@DisplayName("id найден, groupName непуста и уже существует -> Group with name 'groupName' already exists")
	void updateGroup_whenGroupNameNotBlankAndExist_thenValidationBeingPerformed() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("Group1");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group with name " + request.groupName() + " already exists"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " "})
	@DisplayName("id найден, groupName пустая -> Value can not be empty")
	void updateGroup_whenGroupEmpty_thenValidationBeingPerformed(String groupName) throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName(groupName);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Value can not be empty"));
	}

	@Test
	@DisplayName("id не найден -> Group not found")
	void updateGroup_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("GR2");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void updateGroup_whenIdNull_thenInternalServerError() throws Exception {
		GroupDtoGroupName request = new GroupDtoGroupName("GR3");

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/group/")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id найден, группа пустая -> soft delete")
	void deleteGroup_whenIdFoundAndGroupEmpty_thenSoftDelete() throws Exception {
		studentRepository.deleteAll();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/{id}", group.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(group.getId()))
					.andExpect(MockMvcResultMatchers.jsonPath("$.groupName").value("Group1"))
					.andExpect(MockMvcResultMatchers.jsonPath("$.students").isEmpty());

		List<Group> groupList = groupRepository.findAll();
		assertThat(groupList).isEmpty();
		assertThat(groupRepository.isDeletedById(group.getId())).isTrue();
	}

	@Test
	@DisplayName("id найден, группа непустая -> Deletion is not possible. You must disband the group first")
	void deleteGroup_whenIdFoundAndGroupNotEmpty_thenValidationBeingPerformed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/{id}", group.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Deletion is not possible. You must disband the group first"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void deleteGroup_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id не найден -> Group not found")
	void deleteGroup_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/group/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Group not found"));
	}

	@Test
	@DisplayName("id найден, student из списка существует -> добавляем студента в группу")
	void addStudentToGroup_whenIdFoundAndStudentExist_thenAddStudentToGroup() throws Exception {
		Student student2 =
					studentRepository.save(new Student(null, "Ivan", "Ivanov", false, Set.of(group)));

		GroupDtoSetStudents request = new GroupDtoSetStudents(Set.of(student2.getId(), student.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.students",
								Matchers.containsInAnyOrder("Ivan Ivanov", "Name Surname")));

		List<Group> groupList = groupRepository.findAll();
		assertThat(groupList).hasSize(1);
		Optional<Group> groupOpt = groupRepository.findByIdWithStudents(group.getId());
		assertThat(groupOpt).isPresent();
		assertThat(groupOpt.get().getStudents())
					.hasSize(2)
					.extracting(s -> s.getName() + " " + s.getSurname())
					.containsExactlyInAnyOrder("Ivan Ivanov", "Name Surname");
	}

	@Test
	@DisplayName("id найден, список null ->  Ничего не добавляем, возвращаем группу как есть")
	void addStudentToGroup_whenListStudentNull_thenThrowEntityNotFound() throws Exception {
		GroupDtoSetStudents request = new GroupDtoSetStudents(null);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated())
					.andExpect(MockMvcResultMatchers.jsonPath("$.students",
								Matchers.containsInAnyOrder("Name Surname")));

		List<Group> groupList = groupRepository.findAll();
		assertThat(groupList).hasSize(1);
		Optional<Group> groupOpt = groupRepository.findByIdWithStudents(group.getId());
		assertThat(groupOpt).isPresent();
		assertThat(groupOpt.get().getStudents())
					.hasSize(1)
					.extracting(s -> s.getName() + " " + s.getSurname())
					.containsExactlyInAnyOrder("Name Surname");
	}

	@Test
	@DisplayName("id найден, student из списка не существует ->  Student not found")
	void addStudentToGroup_whenStudentNotExist_thenThrowEntityNotFound() throws Exception {
		GroupDtoSetStudents request = new GroupDtoSetStudents(Set.of(1111L, student.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Student not found"));
	}

	@Test
	@DisplayName("id не найден -> Group not found")
	void addStudentToGroup_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		GroupDtoSetStudents request = new GroupDtoSetStudents(Set.of());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void addStudentToGroup_whenIdNull_thenInternalServerError() throws Exception {
		GroupDtoSetStudents request = new GroupDtoSetStudents(Set.of(student.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/group/")
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}
}