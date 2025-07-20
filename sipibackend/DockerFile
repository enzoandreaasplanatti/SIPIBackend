# Imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos los archivos del proyecto
COPY . .

# Construimos el proyecto con Maven Wrapper
RUN ./mvnw clean package -DskipTests

# Puerto en el que corre Spring Boot
EXPOSE 8080

# Comando que se ejecuta al iniciar el contenedor
CMD ["java", "-jar", "target/sipibackend-0.0.1-SNAPSHOT.jar"]
