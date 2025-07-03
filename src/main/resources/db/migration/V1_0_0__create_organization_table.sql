CREATE TABLE u_announce.organization (
    id bigserial primary key,
    slug varchar not null unique,
    display_name varchar not null,
    keycloak_group_id varchar not null,
    keycloak_created_by_user_id varchar not null,
    created_time timestamptz not null
);

CREATE SEQUENCE u_announce.organization_seq increment by 1 start with 1;