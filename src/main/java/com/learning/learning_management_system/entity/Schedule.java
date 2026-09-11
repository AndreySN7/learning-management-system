package com.learning.learning_management_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "schedule")
@Builder
@SQLRestriction(value = "deleted = false")
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "group_id")
	private Long groupId;

	@Column(name = "teacher_id")
	private Long teacherId;

	@Column(name = "course_id")
	private Long courseId;

	@Column(name = "class_start_date")
	private LocalDate startDate;

	@Column(name = "class_end_date")
	private LocalDate endDate;

	@Column(columnDefinition = "boolean default false")
	private boolean deleted;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", insertable = false, updatable = false)
	private Group group;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", insertable = false, updatable = false)
	private Course course;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "teacher_id", insertable = false, updatable = false)
	private Teacher teacher;
}
