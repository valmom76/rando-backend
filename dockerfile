# Estágio 1: Build da aplicação
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copia apenas os arquivos necessários para baixar as dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Imagem final para execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão
EXPOSE 8080

# Comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]