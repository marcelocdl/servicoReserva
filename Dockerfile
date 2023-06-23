FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY . /app/
COPY target/servicoReserva-1.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "servicoReserva-1.0-SNAPSHOT.jar"]
