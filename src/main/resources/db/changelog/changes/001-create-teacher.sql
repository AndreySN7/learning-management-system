CREATE TABLE teacher (
    id bigint generated always as identity primary key unique,
    name varchar(50) not null,
    surname varchar(50)
);