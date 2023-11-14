# Giai đoạn 1: Sử dụng Maven để tải dependencies và tạo tệp .jar
FROM maven:3.8.4-openjdk-11 AS builder
WORKDIR /app
COPY . .
COPY ../settings.xml /root/.m2/settings.xml
RUN mvn -B clean dependency:list install

# Giai đoạn 2: Sử dụng Java runtime để chạy ứng dụng
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/service.jar /app/service.jar
CMD ["java", "-jar", "product-service.jar"]