FROM eclipse-temurin:17-jdk-jammy as build
WORKDIR /app
COPY . .
RUN cd controlsystem && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/controlsystem/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]