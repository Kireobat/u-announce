# uAnnounce

## Table of Contents

- [uAnnounce](#uannounce)
  - [Table of Contents](#table-of-contents)
  - [Local development](#local-development)
    - [Prerequisite](#prerequisite)
      - [Devtools](#devtools)
      - [Other software needed](#other-software-needed)
    - [Steps](#steps)
    - [Add support for new platforms](#add-support-for-new-platforms)
      - [Create a new data class](#create-a-new-data-class)
      - [Update the platform table](#update-the-platform-table)
      - [Write other code](#write-other-code)
  - [Running in prod](#running-in-prod)
    - [Docker](#docker)
  - [ideas](#ideas)

## Local development

### Prerequisite

#### Devtools

- Kotlin 2.2.0
- Java 21
- Maven 3.9.10
- Docker

#### Other software needed

- Keycloak ([required setup](docs/keycloak.md))
- MinIO ([required setup](docs/minio.md))

### Steps

This program uses spring-docker-compose, so as long as you have docker you should only need to click run or run `mvn spring-boot:run` in your cli.

However, if you only want to start the docker containers, it can be done like this:

To create and start: `docker-compose -f .\docker-compose-local.yml up --build`

To stop and remove: `docker-compose -f .\docker-compose-local.yml down`

No environment variables should be necessary for local development.

### Add support for new platforms

If you want to contribute by adding support for other platforms you'll need to do the following in addition to writing the integration

#### Create a new data class

Create an object like this in `eu.kireobat.u_announce.common.credentials`:

```kotlin
package eu.kireobat.u_announce.common.credentials

data class YourPlatform(
    val yourToken: String,
    // other fields...
): Credentials
```

It must extend the `Credentials` sealed interface to work

#### Update the platform table

Add a migration file i.e. `V1_0_X__add_support_for_platform`

```sql
insert into u_announce.platform (slug, display_name, class_name) values ('your-platform', 'Your Platform', 'eu.kireobat.u_announce.common.credentials.YourPlatform');
```

#### Write other code

Now you are ready to write the rest of your new integration

## Running in prod

### Docker

To run the program in docker with the prod profile, you will need the following variables (with example data):

```dotenv
KEYCLOAK_REALM=my-realm
KEYCLOAK_SERVER_URL=https://keycloak.example.com
FRONTEND_KEYCLOAK_CLIENT_ID=frontend-client-id
FRONTEND_SERVER_URL=https://example.com
BACKEND_KEYCLOAK_CLIENT_ID=backend-client-id
BACKEND_KEYCLOAK_CLIENT_SECRET=verySecretSecret
BACKEND_SERVER_URL=https://api.example.com
MASTER_KEY=qUoQO5W7KwbCGiZh/VucOsQpdg+9B53n5VXkfMY9HF8=
```

You will also need to bind any host port to container port `8080`

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
