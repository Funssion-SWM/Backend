FROM openjdk:17-alpine

#Work Directory 설정하여서 어플리케이션 소스들을 해당 디렉토리로 관리
WORKDIR /usr/src/app

ARG JAR_PATH=./build/libs

#Copy로 호스트 pc(ec2 서버)에서 컨테이너로 파일을 복사
COPY ${JAR_PATH}/Inforum-0.0.1-SNAPSHOT.jar ${JAR_PATH}/Inforum-0.0.1-SNAPSHOT.jar

#복사된 jar 파일을 컨테이너 내에서 실행
CMD ["java", "-jar", "./build/libs/Inforum-0.0.1-SNAPSHOT.jar"]
