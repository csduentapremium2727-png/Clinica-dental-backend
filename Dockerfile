# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen con Maven y Java 17 para compilar
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos todo el código fuente al contenedor
COPY . .

# Compilamos el proyecto y generamos el .jar (saltando los tests para evitar errores de conexión en el build)
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
# Usamos una imagen ligera de Java 17 solo para correr la app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiamos el .jar generado en la etapa anterior y lo renombramos a app.jar
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto 8080 (que usa tu Spring Boot internamente)
EXPOSE 8080

# Comando para iniciar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]