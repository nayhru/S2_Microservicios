# =====================================================================
# Stage 1 - Build: compila el JAR con Maven + JDK 21
# =====================================================================
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copia el pom primero para aprovechar la cache de capas de Docker
COPY pom.xml .

# Copia el codigo fuente y compila el JAR ejecutable
COPY src ./src
RUN mvn -B clean package -DskipTests

# =====================================================================
# Stage 2 - Runtime: imagen ligera con solo JRE para ejecutar el JAR
# =====================================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Toma el JAR construido en la etapa anterior
COPY --from=build /app/target/ms-citas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
