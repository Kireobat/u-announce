FROM postgres:17.5-alpine3.22

ADD /src/test/resources/db/postgres_testcontainer_pre_migration_script.sql /docker-entrypoint-initdb.d/0_init.sql