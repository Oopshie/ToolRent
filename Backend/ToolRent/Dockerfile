FROM eclipse-temurin:17-jdk
ARG JAR_FILE=./target/*.jar
COPY ${JAR_FILE} toolRent.jar
ENTRYPOINT ["java","-jar","/toolRent.jar"]