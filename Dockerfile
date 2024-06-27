FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

FROM openjdk:21
COPY --from=build /app/build/libs/*SNAPSHOT.jar /app/application.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/application.jar"]