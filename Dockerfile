FROM sapmachine:24-jdk-ubuntu-noble AS build
WORKDIR /build
COPY . .
RUN ./mvnw clean package -DskipTests

FROM sapmachine:24-jre-ubuntu-noble AS runtime
WORKDIR /app
COPY --from=build /build/target/EzyShop-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
