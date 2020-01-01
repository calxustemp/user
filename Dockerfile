FROM anapsix/alpine-java:8

EXPOSE 4567

ADD target/user-jar-with-dependencies.jar /user-jar-with-dependencies.jar

ENTRYPOINT ["java", "-jar", "/user-jar-with-dependencies.jar"]