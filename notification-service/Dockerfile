
FROM maven:3.9-eclipse-temurin-17 AS BUILD
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn install package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/notification-service app.jar
EXPOSE 8088
ENTRYPOINT ["java","-jar","app.jar"]