#!/bin/bash
# 빌드 파일의 이름이 콘텐츠와 다르다면 다음 줄의 .jar 파일 이름을 수정하시기 바랍니다.

BUILD_JAR=$(ls /var/lib/jenkins/workspace/pre-project/server/build/libs/sof-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)

sudo echo "> 현재 시간: $(date)" >> /home/ec2-user/log/deploy.log

sudo echo "> build 파일명: $JAR_NAME" >> /home/ec2-user/log/deploy.log

sudo echo "> build 파일 복사" >> /home/ec2-user/log/deploy.log
DEPLOY_PATH=/home/ec2-user/build/
sudo cp $BUILD_JAR $DEPLOY_PATH

sudo echo "> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/log/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  sudo echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >>  /home/ec2-user/log/deploy.log
else
  sudo echo "> kill -9 $CURRENT_PID" >> /home/ec2-user/log/deploy.log
  sudo kill -9 $CURRENT_PID
  sudo sleep 5
fi


DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
sudo echo "> DEPLOY_JAR 배포"    >> /home/ec2-user/log/deploy.log
sudo java -jar $DEPLOY_JAR >> /home/ec2-user/log/deploy.log 2>/home/ec2-user/log/deploy_err.log &