FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY . .
RUN ./controlsystem/mvnw clean package -DskipTests -f controlsystem/pom.xml
EXPOSE 8080
CMD ["java", "-jar", "controlsystem/target/*.jar"]