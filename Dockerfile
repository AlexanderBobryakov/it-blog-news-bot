# build Java
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar -x test --no-daemon

FROM openjdk:21-ea-32-bookworm
RUN apt-get update && apt-get install -y python3 python3-pip curl supervisor
RUN pip3 install flask --break-system-packages

COPY --from=build /app/build/libs/*SNAPSHOT-all.jar /app/application.jar
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf
COPY healthcheck.py /app/healthcheck.py

EXPOSE 8080

CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]

# for local test:
# docker build -t my-java-app .
# docker run -p 8080:8080 --name myapp-container my-java-app