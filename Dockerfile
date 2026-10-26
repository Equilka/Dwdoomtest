FROM gradle:jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle --no-daemon shadowJar

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-XX:MaxRAMPercentage=70", "-Duser.timezone=Europe/Kyiv", "-jar", "app.jar"]