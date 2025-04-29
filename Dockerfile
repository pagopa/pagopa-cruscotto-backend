#
# Build
#
FROM maven:3.9.5-amazoncorretto-21-al2023 as buildtime
WORKDIR /build
COPY . .
RUN mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:21.0.1-alpine3.18 as builder
COPY --from=buildtime /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar -Dspring.profiles.active=prod application.jar extract


FROM ghcr.io/pagopa/docker-base-springboot-openjdk17:latest
ADD --chown=spring:spring https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.25.1/opentelemetry-javaagent.jar .

COPY --chown=spring:spring  --from=builder dependencies/ ./
# COPY --chown:spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
# COPY --chown:spring:spring  --from=builder spring-boot-loader/ ./
# COPY --chown:spring:spring  --from=builder application/ ./

EXPOSE 8080

ENTRYPOINT ["java","-javaagent:opentelemetry-javaagent.jar","--enable-preview","org.springframework.boot.loader.JarLauncher"]
