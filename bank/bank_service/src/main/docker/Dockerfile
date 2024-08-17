FROM eclipse-temurin:11 as jre-build
COPY target/ /usr/src
WORKDIR /usr/src/
CMD java -Djava.net.preferIPv4Stack=true\
         -Dswarm.http.port=---port---\
         -Djava.net.preferIPv4Addresses=true\
         -jar bank-thorntail.jar
