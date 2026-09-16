package com.learning.learning_management_system.scheduler;

import com.learning.learning_management_system.entity.Schedule;
import com.learning.learning_management_system.repository.CourseScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class CleanUpScheduler {
	private final CourseScheduleRepository courseScheduleRepository;

	@Scheduled(cron = "${app.cleanup.cron:-}")
	@Transactional
	public void removeCourseSchedule() {
		LocalDate verificationDate = LocalDate.now().minusYears(1);
		List<Schedule> allByEndDateBefore = courseScheduleRepository.findAllByEndDateBefore(verificationDate);

		if (allByEndDateBefore.isEmpty()) {
			log.info("Course schedule not found to delete");
		} else {
			courseScheduleRepository.deleteAll(allByEndDateBefore);
			log.info("Course schedule has been removed ({} records)", allByEndDateBefore.size());
		}
	}
}
