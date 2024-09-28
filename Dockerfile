# 1. Maven kullanarak projeyi derlemek için Maven imajını kullanın
        FROM maven:3.8.5-openjdk-17 AS build

        # 2. Proje dosyalarını kopyalayın
        COPY src /Users/umutkilic/Downloads/EchoBot-Example-master/src
        COPY pom.xml /Users/umutkilic/Downloads/EchoBot-Example-master/pom.xml

        # 3. Maven ile projeyi derleyin
        RUN mvn -f /Users/umutkilic/Downloads/EchoBot-Example-master/pom.xml clean package

        # 4. Son aşamada JAR dosyasını çalıştırmak için OpenJDK imajını kullanın
        FROM openjdk:17-jdk-slim

        # 5. Derlenen JAR dosyasını kopyalayın
        COPY --from=build /Users/umutkilic/Downloads/EchoBot-Example-master/target/echobotexample-0.0.1-SNAPSHOT.jar /app/echobotexample-0.0.1-SNAPSHOT.jar

        # 6. Portu açın
        EXPOSE 5000

        # 7. Uygulamayı çalıştırın
        CMD ["java", "-jar", "/app/my-java-app.jar"]