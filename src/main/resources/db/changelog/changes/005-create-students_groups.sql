CREATE TABLE students_groups (
    group_id bigint,
    student_id bigint,
    constraint pk_students_groups primary key (student_id, group_id),
    constraint fk_students_groups_group foreign key (group_id) references groups (id),
    constraint fk_students_groups_student foreign key (student_id) references student (id)
);