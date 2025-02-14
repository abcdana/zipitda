# 1. JDK 17 기반 이미지 사용
FROM openjdk:17-jdk

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/zipitda-0.0.1-SNAPSHOT.jar app.jar

# 4. 환경 변수 설정 (docker-compose.yml에서 설정한 값 사용)
ENV SPRING_APPLICATION_NAME=zipitda
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/zipitda?useSSL=false&characterEncoding=UTF-8&serverTimezone=Asia/Seoul
ENV SPRING_DATASOURCE_USERNAME=zipitda_user
ENV SPRING_DATASOURCE_PASSWORD=password123

# 5. 컨테이너 실행 시 Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]