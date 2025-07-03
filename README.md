# uAnnounce

## Local development

### Prerequisites

#### Devtools

- Kotlin 2.2.0
- Java 21
- Maven 3.9.10
- Docker

#### Other infrastructure

- Keycloak
- MinIO

### Steps

1. Env variables

You need to create a `.env` file with the following variables in the root project folder

```dotenv
U_ANNOUNCE_KEYCLOAK_CLIENT_ID=
U_ANNOUNCE_KEYCLOAK_CLIENT_SECRET=
```

If you use IntelliJ IDEA it may be necessary to download a plugin called [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile) and edit your Run/Debug configuration to use the `.env` file


## ideas

- invite to org
  - invite link that expires after x days or x uses
  - when creating link have option "verify: boolean" if users clicking link need to be accepted by org admins
- better error handling
- new tables
  - media_type
    - id bigserial primary key
    - name varchar not null --eg. "JPEG"
    - accepted_formats jsonb --string[] eg. [".jpg",".jpeg",".jpe",".jif",".jfif",".jfi"]
  - media (configurable constraints (temporary storage, last 10 rows, max size))
    - id bigserial primary key
    - media_type_id biginteger references u_announce.media_type(id) not null
    - storage_path varchar not null --url to minio thing?
    - organization_id biginteger references u_announce.organization(id) not null
    - keycloak_created_by_user_id varchar not null
    - created_time timestamptz not null
  - platform
    - id bigserial primary key
    - platform_name varchar not null
  - platform_map_media_type
    - id bigserial primary key
    - platform_id biginteger references u_announce.platform(id) not null
    - media_type_id biginteger references u_announce.media_type(id) not null
  - announcement
    - id bigserial primary key
    - organization_id biginteger references u_announce.organization(id) not null
    - keycloak_created_by_user_id varchar not null
    - created_time timestamptz not null
  - announcement_map_platform
    - id bigserial primary key
    - announcement_id biginteger references u_announce.announcement(id) not null
    - platform_id biginteger references u_announce.platform(id) not null
  - announcement_map_media
      - id bigserial primary key
      - announcement_id biginteger references u_announce.announcement(id) not null
      - media_id biginteger references u_announce.media(id) not null
  - secrets UNIQUE(organization_id, platform_id)
    - id bigserial primary key
    - organization_id biginteger references u_announce.organization(id) not null
    - platform_id biginteger references u_announce.platform(id) not null
    - client_id varchar not null --encrypted
    - client_secret varchar not null --encrypted
    - keycloak_created_by_user_id varchar not null
    - created_time timestamptz not null