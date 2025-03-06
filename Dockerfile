FROM maven:latest as build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-oracle
WORKDIR /opt/app
COPY --from=build /app/target/photo_hub-*.jar ./photo_hub.jar

EXPOSE 8081

CMD ["java", "-jar", "photo_hub.jar"]
