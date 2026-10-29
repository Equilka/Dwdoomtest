FROM gradle:jdk25 AS build
WORKDIR /app
COPY . .
RUN gradle --no-daemon shadowJar

FROM eclipse-temurin:25-jre
WORKDIR /app

RUN apt-get update \
 && apt-get install -y --no-install-recommends ffmpeg curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*

RUN ARCH=$(uname -m) \
 && if [ "$ARCH" = "aarch64" ]; then BIN=yt-dlp_linux_aarch64; else BIN=yt-dlp_linux; fi \
 && curl -fL "https://github.com/yt-dlp/yt-dlp/releases/latest/download/$BIN" -o /usr/local/bin/yt-dlp \
 && chmod +x /usr/local/bin/yt-dlp \
 && yt-dlp --version

COPY --from=build /app/build/libs/*-all.jar app.jar
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-XX:MaxRAMPercentage=70", "-Duser.timezone=Europe/Kyiv", "-jar", "app.jar"]