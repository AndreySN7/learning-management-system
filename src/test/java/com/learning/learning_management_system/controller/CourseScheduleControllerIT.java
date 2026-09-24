package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoCourseTime;
import com.learning.learning_management_system.dto.CourseSchedule.CourseScheduleDtoGroupToCourse;
import com.learning.learning_management_system.entity.Course;
import com.learning.learning_management_system.entity.Group;
import com.learning.learning_management_system.entity.Schedule;
import com.learning.learning_management_system.entity.Teacher;
import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CourseScheduleControllerIT extends AbstractIntegrationTest {

	@Autowired
	private CourseScheduleRepository courseScheduleRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private TeacherRepository teacherRepository;
	@Autowired
	private CourseRepository courseRepository;

	private Group group, groupInScheduleRepo;
	private Teacher teacher;
	private Course course1, course2;
	private Schedule schedule;

	@BeforeEach
	void setUp() {
		group = new Group(null, "Group", false, new HashSet<>());
		groupRepository.save(group);

		groupInScheduleRepo = new Group(null, "Group2", false, new HashSet<>());
		groupRepository.save(groupInScheduleRepo);
		teacher = new Teacher(null, "Teacher", "Surname", false);
		teacherRepository.save(teacher);
		course1 = new Course(null, "Course1", "Description1", false);
		course2 = new Course(null, "Course2", "", false);
		courseRepository.saveAll(List.of(course1, course2));
		schedule = Schedule.builder()
					.groupId(groupInScheduleRepo.getId())
					.teacherId(teacher.getId())
					.courseId(course1.getId())
					.build();
		courseScheduleRepository.save(schedule);
	}

	@AfterEach
	void cleanAfterTest() {
		courseScheduleRepository.deleteAll();
		courseRepository.deleteAll();
		groupRepository.deleteAll();
		teacherRepository.deleteAll();
	}

	@Test
	@DisplayName("id_group найден, teacherId найден, coursesIds найден -> добавляем расписание")
	void addGroupToCourse_whenEverythingValid_thenAddSchedule() throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(teacher.getId(), Set.of(course1.getId(), course2.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated());

		Page<Schedule> schedules =
					courseScheduleRepository.findAllByGroupId(group.getId(), PageRequest.of(0, 10));
		assertThat(schedules)
					.hasSize(2)
					.extracting(sch -> sch.getCourse().getName())
					.containsExactlyInAnyOrder("Course1", "Course2");
		assertThat(schedules).extracting(Schedule::getTeacherId).containsOnly(teacher.getId());
	}

	@Test
	@DisplayName("id_group найден, teacherId null, coursesIds найден -> добавляем расписание")
	void addGroupToCourse_whenEverythingValidTeacherNull_thenAddSchedule() throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(null, Set.of(course1.getId(), course2.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated());

		Page<Schedule> schedules =
					courseScheduleRepository.findAllByGroupId(group.getId(), PageRequest.of(0, 10));
		assertThat(schedules)
					.hasSize(2)
					.extracting(sch -> sch.getCourse().getName())
					.containsExactlyInAnyOrder("Course1", "Course2");
		assertThat(schedules).extracting(Schedule::getTeacherId).containsOnlyNulls();
	}

	@Test
	@DisplayName("id_group найден, teacherId найден или null, coursesIds не найден -> операция успешна, но ничего не добавится")
	void addGroupToCourse_whenCoursesNotFound_thenNoSchedule() throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(teacher.getId(), Set.of(1111L, 1112L));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isCreated());

		Page<Schedule> schedules =
					courseScheduleRepository.findAllByGroupId(group.getId(), PageRequest.of(0, 10));
		assertThat(schedules).isEmpty();
	}

	@Test
	@DisplayName("id_group найден, teacherId не найден, coursesIds любой -> Teacher not found")
	void addGroupToCourse_whenTeacherNotFound_thenThrowEntityNotFound() throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(1111L, Set.of(course1.getId(), course2.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", group.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Teacher not found"));
	}

	@Test
	@DisplayName("id_group не найден -> Group not found")
	void addGroupToCourse_whenGroupNotFound_thenThrowEntityNotFound() throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(teacher.getId(), Set.of(course1.getId(), course2.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = {"", " ", "y"})
	@DisplayName("id_group null или 'буквы' -> Внутренняя ошибка сервера")
	void addGroupToCourse_whenGroupNotValid_thenInternalServerError(String groupId) throws Exception {
		CourseScheduleDtoGroupToCourse request =
					new CourseScheduleDtoGroupToCourse(teacher.getId(), Set.of(course1.getId(), course2.getId()));

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/course-schedule/group/{id}", groupId)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id найден, дата соответствует формату, дата начала < даты конца -> обновляем даты")
	void updateCourseTimeForGroup_whenDatesValid_thenUpdateDates() throws Exception {
		CourseScheduleDtoCourseTime request = new CourseScheduleDtoCourseTime(
					LocalDate.parse("2026-09-01"),
					LocalDate.parse("2026-10-01"));
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", schedule.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk());

		Optional<Schedule> scheduleOpt = courseScheduleRepository.findById(schedule.getId());
		assertThat(scheduleOpt).isPresent();
		assertThat(scheduleOpt.get().getStartDate()).isEqualTo(LocalDate.parse("2026-09-01"));
		assertThat(scheduleOpt.get().getEndDate()).isEqualTo(LocalDate.parse("2026-10-01"));
	}

	@Test
	@DisplayName("id найден, дата соответствует формату, дата начала есть, даты конца  null-> обновляем даты")
	void updateCourseTimeForGroup_whenEndDateNull_thenUpdateDates() throws Exception {
		CourseScheduleDtoCourseTime request = new CourseScheduleDtoCourseTime(
					LocalDate.parse("2026-09-01"), null);
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", schedule.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isOk());

		Optional<Schedule> scheduleOpt = courseScheduleRepository.findById(schedule.getId());
		assertThat(scheduleOpt).isPresent();
		assertThat(scheduleOpt.get().getStartDate()).isEqualTo(LocalDate.parse("2026-09-01"));
		assertThat(scheduleOpt.get().getEndDate()).isNull();
	}

	@Test
	@DisplayName("id найден, дата соответствует формату, дата начала null, дата конца присутствует -> " +
				"The start date cannot be empty if the end date exists")
	void updateCourseTimeForGroup_whenStartDatesNullAndEndDateValid_thenValidationBeingPerformed() throws Exception {
		CourseScheduleDtoCourseTime request = new CourseScheduleDtoCourseTime(
					null, LocalDate.parse("2026-09-01"));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", schedule.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("The start date cannot be empty if the end date exists"));
	}

	@Test
	@DisplayName("id найден, дата соответствует формату, дата начала > даты конца -> " +
				"The end date cannot be less than the start date")
	void updateCourseTimeForGroup_whenStartAfterEnd_thenValidationBeingPerformed() throws Exception {
		CourseScheduleDtoCourseTime request = new CourseScheduleDtoCourseTime(
					LocalDate.parse("2026-09-01"),
					LocalDate.parse("2026-08-01"));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", schedule.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("The end date cannot be less than the start date"));
	}

	@Test
	@DisplayName("id найден, дата не соответствует формату -> Дата должна быть в формате yyyy-MM-dd")
	void updateCourseTimeForGroup_whenDateNotValid_thenValidationBeingPerformed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", schedule.getId())
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
											{
												"startDate": "2026-09-01",
												"endDate": "2026/09/01"
											}"""))
					.andExpect(MockMvcResultMatchers.status().isBadRequest())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Дата должна быть в формате yyyy-MM-dd"));
	}

	@Test
	@DisplayName("id не найден -> CourseSchedule not found")
	void updateCourseTimeForGroup_whenIdNotFound_thenThrowEntityNotFound() throws Exception {
		CourseScheduleDtoCourseTime request = new CourseScheduleDtoCourseTime(
					LocalDate.parse("2026-09-01"),
					LocalDate.parse("2026-10-01"));

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/{id}", 1111L)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(request)))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("CourseSchedule not found"));
	}

	@Test
	@DisplayName("id null -> Внутренняя ошибка сервера")
	void updateCourseTimeForGroup_whenIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/course-schedule/")
								.contentType(MediaType.APPLICATION_JSON)
								.content("""
											{
												"startDate": "2026-09-01",
												"endDate": "2026/09/01"
											}
											"""))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message")
								.value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id_group найден -> возвращаем курсы")
	void getScheduleCourseForGroup_whenGroupIdFound_thenReturnSchedule() throws Exception {
		Schedule newSchedule = Schedule.builder()
					.groupId(groupInScheduleRepo.getId())
					.teacherId(teacher.getId())
					.courseId(course2.getId())
					.build();
		courseScheduleRepository.save(newSchedule);

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/group/{id}", groupInScheduleRepo.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());

		Page<Schedule> response = courseScheduleRepository.findAllByGroupId(
					groupInScheduleRepo.getId(), PageRequest.of(0, 10));

		assertThat(response).hasSize(2);
		assertThat(response.getContent())
					.extracting(sch -> sch.getCourse().getName())
					.containsExactlyInAnyOrder(course1.getName(), course2.getName());
		assertThat(response.getContent())
					.extracting(sch -> sch.getTeacher().getName())
					.containsOnly(teacher.getName());
	}

	@Test
	@DisplayName("id_group не найден -> Group not found")
	void getScheduleCourseForGroup_whenGroupIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/group/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Group not found"));
	}

	@Test
	@DisplayName("id_group null -> Внутренняя ошибка сервера")
	void getScheduleCourseForGroup_whenGroupIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

	@Test
	@DisplayName("id_teacher найден -> возвращаем курсы")
	void getScheduleClassesForTeacher_whenTeacherIdFound_thenReturnSchedule() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/teacher/{id}", teacher.getId())
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isOk());

		Page<Schedule> response = courseScheduleRepository.findAllByTeacherId(
					teacher.getId(), PageRequest.of(0, 10));

		assertThat(response).hasSize(1);
		assertThat(response.getContent())
					.extracting(sch -> sch.getCourse().getName())
					.containsExactlyInAnyOrder(course1.getName());
		assertThat(response.getContent())
					.extracting(sch -> sch.getTeacher().getName())
					.containsOnly(teacher.getName());
	}

	@Test
	@DisplayName("id_teacher не найден -> Teacher not found")
	void getScheduleClassesForTeacher_whenTeacherIdNotFound_thenThrowEntityNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/teacher/{id}", 1111L)
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isNotFound())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Teacher not found"));
	}

	@Test
	@DisplayName("id_teacher null -> Внутренняя ошибка сервера")
	void getScheduleClassesForTeacher_whenTeacherIdNull_thenInternalServerError() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/course-schedule/")
								.accept(MediaType.APPLICATION_JSON))
					.andExpect(MockMvcResultMatchers.status().isInternalServerError())
					.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Внутренняя ошибка сервера"));
	}

}