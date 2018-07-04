FROM openjdk:8-jre
VOLUME /tmp
COPY context/@{project.build.finalName}.jar app.jar
# todo: update jrebel as soon as a new version comes out, because current 7.1.7 version causes errors in logs
COPY context/jrebel /
ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar

