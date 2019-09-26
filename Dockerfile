FROM alpine/git as clone
WORKDIR /app
RUN git clone https://github.com/ManuelGuillen89/beers-api

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /app
COPY --from=clone /app/beers-api /app
RUN mvn clean install -DskipTests

FROM openjdk:8-jdk-alpine
VOLUME /tmp
EXPOSE 8080
#ADD ./waitForMySQLAndStart.sh /root/
#RUN chmod +x /root/waitForMySQLAndStart.sh
ARG JAR_FILE=target/beers-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]