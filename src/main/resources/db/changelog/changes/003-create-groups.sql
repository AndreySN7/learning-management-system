CREATE TABLE groups (
    id bigint generated always as identity primary key unique,
    group_name varchar(50) not null
);