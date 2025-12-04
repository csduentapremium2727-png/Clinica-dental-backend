# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen de Maven para compilar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos todo el código fuente al contenedor
COPY . .

# Compilamos el proyecto y generamos el .jar
# (Saltamos tests para agilizar y evitar errores de conexión en build)
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# 🚨 CORRECCIÓN: Usamos Eclipse Temurin (versión Alpine ligera) en lugar de openjdk:17-jdk-slim
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiamos el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]