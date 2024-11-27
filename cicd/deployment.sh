#!/bin/bash

# 실행 중인 프로세스 찾기 (backend JAR 기준)
spring_pid=$(ps -ef | grep 'backend-0.0.1-SNAPSHOT.jar' | grep -v grep | awk '{print $2}')

if [ -n "$spring_pid" ]; then
    echo "실행 중인 백엔드 프로세스($spring_pid) 종료하는 중..."
    kill -9 "$spring_pid"
else
    echo "실행 중인 백엔드 프로세스가 없습니다!"
fi

# 재실행
echo "스프링 프로젝트 재실행 중..."
nohup java -jar /home/ubuntu/backend/build/libs/backend-0.0.1-SNAPSHOT.jar > /home/ubuntu/backend.log 2>&1 &

# 성공적으로 시작되었는지 확인
sleep 5  # 5초 대기
new_pid=$(ps -ef | grep 'backend-0.0.1-SNAPSHOT.jar' | grep -v grep | awk '{print $2}')
if [ -n "$new_pid" ]; then
    echo "백엔드가 성공적으로 시작되었습니다. (PID: $new_pid)"
else
    echo "백엔드 재실행에 실패했습니다. 로그를 확인하세요."
fi
