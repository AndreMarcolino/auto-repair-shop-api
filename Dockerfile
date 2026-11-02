# Etapa 1: compilação da aplicação
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Corrige finais de linha do Windows e concede permissão de execução
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

COPY src/ src/

RUN ./mvnw -B clean package -DskipTests

# Etapa 2: execução da aplicação
FROM eclipse-temurin:25-jdk AS runtime

WORKDIR /app

# Executa a aplicação com usuário sem privilégios de administrador
RUN groupadd --system app \
    && useradd --system --gid app app

COPY --from=build --chown=app:app /app/target/*.jar /app/app.jar

USER app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]