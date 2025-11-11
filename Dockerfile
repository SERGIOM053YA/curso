# Usa una imagen base oficial de Maven con Java 21 (como tu proyecto)
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de tu proyecto
COPY . .

# Ejecuta Maven para construir el .jar (limpio y sin conexión)
RUN mvn clean install -DskipTests

# --- Segunda Etapa: Crear la imagen final y ligera ---
# Usamos una imagen de Java 21 súper ligera
FROM eclipse-temurin:21-jre-alpine

# Establece el directorio de trabajo
WORKDIR /app

# Copia SOLO el .jar compilado de la etapa anterior
COPY --from=build /app/target/curso-0.0.1-SNAPSHOT.jar ./app.jar

# Expone el puerto 8080 (el que usa Spring Boot)
EXPOSE 8080

# El comando para ejecutar tu aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
