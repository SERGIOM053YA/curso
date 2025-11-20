# Etapa 1: Construcción
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Descarga dependencias primero (mejora cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia el código y construye
COPY . .
RUN mvn -q clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el jar generado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
# Limitamos la memoria a 350MB para evitar el error OOM en Railway
ENTRYPOINT ["java", "-Xmx350m", "-Xms350m", "-jar", "app.jar"]


