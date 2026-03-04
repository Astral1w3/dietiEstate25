FROM maven:3.9.6-eclipse-temurin-21-jammy AS build

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw package -DskipTests


FROM eclipse-temurin:21-jre-jammy

RUN useradd -m -s /bin/bash springuser

WORKDIR /app

RUN mkdir -p /app/uploads && chown -R springuser:springuser /app/uploads

COPY --from=build --chown=springuser:springuser /app/target/*.jar app.jar

USER springuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
