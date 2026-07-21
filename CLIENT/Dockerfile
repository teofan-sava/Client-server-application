# BUILD
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

COPY TEST_ZIP/test.zip ./TEST_ZIP/test.zip

RUN apk add --no-cache bash

COPY --from=builder /build-env/build/libs/CLIENT-*.jar ./client.jar

COPY run-one-client.sh ./run-one-client.sh

RUN chmod +x ./run-one-client.sh

ENV PORT=12345

CMD ["bash", "-c", "./run-one-client.sh $PORT"]
