FROM eclipse-temurin:21-jre-alpine-3.21 AS builder
WORKDIR /application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract


FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher", "-Xmx512M", "-Doracle.jdbc.J2EE13Compliant=true", "-Djava.security.egd=file:/dev/./urandom"]