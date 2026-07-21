#BUILD
FROM gradle:9.3.0-jdk21 AS builder

WORKDIR /build-env

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle.properties ./

RUN gradle dependencies --no-daemon

COPY src ./src

RUN gradle build --no-daemon -x test

# RUN
FROM eclipse-temurin:25-jre-alpine-3.23 AS runner

WORKDIR /app

RUN apk add --no-cache bash

COPY --from=builder /build-env/build/libs/SERVER-*.jar ./server.jar

COPY run-server.sh ./run-server.sh

RUN chmod +x ./run-server.sh

EXPOSE 8080

ENV PORT=12345

CMD ["bash", "-c", "./run-server.sh $PORT"]
