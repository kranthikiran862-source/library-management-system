FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN javac -cp "mysql-connector-j-26.7.0.jar" *.java

EXPOSE 8080

CMD ["java", "--add-modules", "jdk.httpserver", "-cp", ".:mysql-connector-j-26.7.0.jar", "LibraryServer"]
