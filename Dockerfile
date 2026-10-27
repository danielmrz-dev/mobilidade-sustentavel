# ---------- Etapa 1: build com Maven ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependencias primeiro para aproveitar o cache de camadas do Docker
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: imagem final, apenas com o JRE ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sem privilegios de root
RUN addgroup -S app && adduser -S app -G app

COPY --from=build /app/target/mobilidade-api.jar app.jar
RUN chown app:app app.jar
USER app

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
