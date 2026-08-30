CREATE TABLE u_announce.s3_metadata (
    id bigserial primary key,
    media_key varchar not null,
    display_filename varchar not null,
    original_filename varchar not null,
    publish_time timestamptz not null,
    expiry_time timestamptz not null,
    organization_id bigint references u_announce.organization(id) not null,
    keycloak_created_by_user_id varchar not null,
    created_time timestamptz not null
);