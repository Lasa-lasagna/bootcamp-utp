FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -q  -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Las variables DB_URL, DB_USER, DB_PASSWORD, TENANT_ID, CLIENT_ID y
# CLIENT_SECRET se inyectan en tiempo de ejecucion (nunca en la imagen):
#   docker run --env-file local/.env -p 1419:1419 margos-backend
EXPOSE 1419

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

