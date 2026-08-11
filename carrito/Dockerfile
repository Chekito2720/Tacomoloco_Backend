FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace
COPY .mvn ./.mvn
COPY mvnw mvnw.cmd pom.xml ./
COPY src ./src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/carrito-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/app/app.jar"]
