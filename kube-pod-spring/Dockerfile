# 17 버전의 OpenJDK 이미지를 기반으로 새로운 컨테이너 이미지를 빌드하라는 명령
FROM openjdk:17-jdk

# 로컬 머신(또는 CI)에서 build/libs 디렉터리에 생성된 *SNAPSHOT.jar 파일을 이미지의 루트(/)에 app.jar라는 이름으로 복사함
COPY build/libs/*SNAPSHOT.jar /app.jar

# 컨테이너가 시작될 때 기본으로 실행할 명령과 인자를 지정
ENTRYPOINT ["java", "-jar", "/app.jar"]
