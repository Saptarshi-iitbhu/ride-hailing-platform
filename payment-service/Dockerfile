
FROM maven:3.9-eclipse-temurin-17 AS BUILD
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn install package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/payment-service-0.1.0.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","app.jar"]