FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos solo la subcarpeta donde está el código
COPY sipibackend /app

# Le damos permisos y construimos el proyecto
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Exponé el puerto en el que corre tu app
EXPOSE 4002

CMD ["java", "-jar", "target/sipibackend-0.0.1-SNAPSHOT.jar"]
