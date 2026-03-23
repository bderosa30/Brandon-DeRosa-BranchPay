FROM gradle:jdk21 AS builder
WORKDIR /Brandon
COPY --chown=gradle:gradle . /Brandon
RUN gradle clean build --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /Brandon
COPY --from=builder /Brandon/build/libs/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]