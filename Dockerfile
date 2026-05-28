FROM eclipse-temurin:17-jdk-alpine
ARG ARCHIVO_JAR=target/*.jar
COPY ${ARCHIVO_JAR} ticketpro.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/ticketpro.jar"]