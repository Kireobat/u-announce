CREATE TABLE u_announce.platform (
    id bigserial primary key,
    slug varchar not null unique,
    display_name varchar not null,
    class_name varchar not null
);

CREATE SEQUENCE u_announce.platform_seq increment by 1 start with 1;