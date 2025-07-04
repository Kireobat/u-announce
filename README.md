# uAnnounce

## Table of Contents

- [uAnnounce](#uannounce)
  - [Table of Contents](#table-of-contents)
  - [Local development](#local-development)
    - [Prerequisites](#prerequisites)
      - [Devtools](#devtools)
      - [Other software needed](#other-software-needed)
    - [Steps](#steps)
      - [Environment variables](#environment-variables)
    - [Add support for new platforms](#add-support-for-new-platforms)
      - [Create a new data class](#create-a-new-data-class)
      - [Update the platform table](#update-the-platform-table)
      - [Write other code](#write-other-code)
  - [ideas](#ideas)

## Local development

### Prerequisites

#### Devtools

- Kotlin 2.2.0
- Java 21
- Maven 3.9.10
- Docker

#### Other software needed

- Keycloak
- MinIO

### Steps

#### Environment variables

You need to create a `.env` file with the following variables in the root project folder

```dotenv
U_ANNOUNCE_KEYCLOAK_CLIENT_ID=
U_ANNOUNCE_KEYCLOAK_CLIENT_SECRET=
U_ANNOUNCE_MASTER_KEY=
```

If you use IntelliJ IDEA it may be necessary to download a plugin called [EnvFile](https://plugins.jetbrains.com/plugin/7861-envfile) and edit your Run/Debug configuration to use the `.env` file

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