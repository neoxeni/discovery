FROM openjdk:17-jdk

EXPOSE 8081

COPY ./target/*.war app.war

RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app.war"]
