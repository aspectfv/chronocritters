# Builds any of the three Spring Boot services. Pass the module name as SERVICE.
# The build stage is identical for all three, so Docker reuses its layer cache
# across them and the Maven reactor only runs once per code change.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY proto/pom.xml proto/
COPY lib/pom.xml lib/
COPY user/pom.xml user/
COPY lobby/pom.xml lobby/
COPY gamelogic/pom.xml gamelogic/
RUN mvn -B dependency:go-offline -DskipTests || true

COPY proto/src proto/src
COPY lib/src lib/src
COPY user/src user/src
COPY lobby/src lobby/src
COPY gamelogic/src gamelogic/src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
ARG SERVICE
WORKDIR /app
RUN useradd -r -u 1001 app
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-*.jar app.jar
USER app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=60"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
