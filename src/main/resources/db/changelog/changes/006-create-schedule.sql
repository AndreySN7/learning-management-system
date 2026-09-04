CREATE TABLE schedule (
    id bigint generated always as identity primary key unique,
    group_id bigint,
    teacher_id bigint,
    course_id bigint,
    class_start_date date,
    class_end_date date,
    constraint fk_group_id foreign key (group_id) references groups (id),
    constraint fk_teacher_id foreign key (teacher_id) references teacher (id),
    constraint fk_course_id foreign key (course_id) references course (id)
);