# Imagen base ligera con Java 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia el JAR ejecutable construido por Maven (mvn clean package -DskipTests)
COPY target/ms-citas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
