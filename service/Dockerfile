FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8080
ADD /target/service.jar /service.jar
ENTRYPOINT ["java","-jar","/service.jar"]