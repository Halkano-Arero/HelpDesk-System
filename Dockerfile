FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
COPY lib ./lib
COPY src ./src
RUN mvn -DskipTests package

FROM tomcat:9.0-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /build/target/helpdesk.war /usr/local/tomcat/webapps/ROOT.war
COPY --from=build /build/target/helpdesk.war /usr/local/tomcat/webapps/helpdesk.war
COPY docker/wait-and-run.sh /usr/local/bin/wait-and-run.sh
RUN chmod +x /usr/local/bin/wait-and-run.sh

ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/wait-and-run.sh"]
