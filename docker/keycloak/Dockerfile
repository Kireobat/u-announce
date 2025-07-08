# Multi-stage build to process JSONC file
FROM node:24-alpine AS json-processor

# Create a working directory
WORKDIR /app

# Install the jsonc-parser package
RUN npm install jsonc-parser

# Copy JSONC file
COPY ./docker/keycloak/realm-config.jsonc /tmp/realm-config.jsonc

# Create a script to convert JSONC to JSON
RUN echo 'const fs = require("fs");' > convert.js && \
    echo 'const { parse } = require("jsonc-parser");' >> convert.js && \
    echo 'const jsoncContent = fs.readFileSync("/tmp/realm-config.jsonc", "utf8");' >> convert.js && \
    echo 'const json = parse(jsoncContent);' >> convert.js && \
    echo 'fs.writeFileSync("/tmp/realm-config.json", JSON.stringify(json, null, 2));' >> convert.js

# Run the conversion script
RUN node convert.js

# Main Keycloak image
FROM quay.io/keycloak/keycloak:26.3

# Switch to root user to create directories and copy files
USER root

# Create import directory
RUN mkdir -p /opt/keycloak/data/import

# Copy processed JSON file from the previous stage
COPY --from=json-processor /tmp/realm-config.json /opt/keycloak/data/import/realm-config.json

# Change ownership to keycloak user
RUN chown -R keycloak:keycloak /opt/keycloak/data

# Switch back to keycloak user
USER keycloak

# Build Keycloak
RUN /opt/keycloak/bin/kc.sh build

# Expose port
EXPOSE 8080

# Start Keycloak with import
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev", "--import-realm"]