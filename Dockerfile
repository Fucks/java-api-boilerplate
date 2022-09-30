FROM openjdk:11-jdk
ARG VERSION

RUN mkdir /app
WORKDIR /app

ENV JAR_FILE=target/boilerplate*.jar
COPY ${JAR_FILE} /app/application.jar

EXPOSE 9094

ENTRYPOINT ["java","-jar", "application.jar"]