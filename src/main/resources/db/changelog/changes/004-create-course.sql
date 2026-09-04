CREATE TABLE course (
    id bigint generated always as identity primary key unique,
    name varchar(50) not null,
    description varchar(200)
);