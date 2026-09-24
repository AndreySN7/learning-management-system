package com.learning.learning_management_system.controller;

import com.learning.learning_management_system.repository.CourseRepository;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import com.learning.learning_management_system.repository.GroupRepository;
import com.learning.learning_management_system.repository.StudentRepository;
import com.learning.learning_management_system.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

	@Autowired
	private CourseScheduleRepository courseScheduleRepository;
	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private CourseRepository courseRepository;
	@Autowired
	private TeacherRepository teacherRepository;

	@Container
	static final PostgreSQLContainer POSTGRE_SQL_CONTAINER =
				new PostgreSQLContainer("postgres:18")
							.withDatabaseName("learning-management-system")
							.withUsername("test")
							.withPassword("test");

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);
	}

	@BeforeEach
	void cleanAllBase() {
		courseScheduleRepository.deleteAll();
		studentRepository.deleteAll();
		groupRepository.deleteAll();
		courseRepository.deleteAll();
		teacherRepository.deleteAll();
	}
}