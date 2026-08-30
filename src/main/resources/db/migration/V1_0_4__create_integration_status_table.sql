CREATE TABLE u_announce.integration_status (
    id bigserial primary key,
    organization_id bigint references u_announce.organization(id) not null,
    platform_id bigint references u_announce.platform(id) not null,
    active boolean not null,
    keycloak_created_by_user_id varchar not null,
    created_time timestamptz not null,
    keycloak_modified_by_user_id varchar,
    modified_time timestamp
);