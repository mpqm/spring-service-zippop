<template>
    <div>
      <h2>예약 상태 테스트</h2>
      <div v-if="statusMessages.length > 0">
        <h3>상태 메시지 로그:</h3>
        <ul>
          <li v-for="(message, index) in statusMessages" :key="index">
            접속자: {{ message.workingTotal }}, 대기자: {{ message.waitingTotal }}, 상태: {{ message.statusMessage }}
          </li>
        </ul>
      </div>
      <button @click="sendStatusRequest">예약 상태 요청</button>
    </div>
  </template>
  
  <script>
  import SockJS from 'sockjs-client';
  import { Stomp } from '@stomp/stompjs';
  import { useAuthStore } from '@/stores/useAuthStore';
  export default {
    data() {
      return {
        stompClient: null,
        statusMessages: [], // 상태 메시지들을 저장할 배열
      };
    },
    mounted() {
      this.connectWebSocket();
    },
    methods: {
        connectWebSocket() {
  // WebSocket 연결을 위한 SockJS 인스턴스 생성
  const socket = new SockJS('http://localhost:8080/ws'); // 엔드포인트에 맞게 수정
  this.stompClient = Stomp.over(socket);

  // WebSocket 연결 설정
  this.stompClient.connect({}, (frame) => {
    console.log('Connected: ' + frame);

    // 구독 설정 - 서버가 특정 사용자에게 보내는 경로 구독
    const userEmail = useAuthStore().userInfo.email; // 사용자의 이메일을 동적으로 가져오도록 변경 필요
    
    this.stompClient.subscribe(`/user/${userEmail}/reserve/status`, (message) => {
      console.log('Raw message received: ', message);
      if (message.body) {
        try {
          const data = JSON.parse(message.body);
          this.statusMessages.push({
            workingTotal: data.workingTotal,
            waitingTotal: data.waitingTotal,
            statusMessage: data.statusMessage
          });
          console.log('Parsed message: ', data);
        } catch (error) {
          console.error('Failed to parse message:', error);
        }
      } else {
        console.log('Received empty message or unable to parse.');
      }
    });

  }, (error) => {
    console.error('WebSocket 연결 실패: ', error);
    // 연결 실패 시 재연결 시도 (5초 후)
    setTimeout(() => {
      this.connectWebSocket();
    }, 5000);
  });
},
      
      sendStatusRequest() {
        // 예약 상태 요청 메시지 발행
        if (this.stompClient && this.stompClient.connected) {
          const reserveIdx = 14; // 예시 예약 ID (이 값을 동적으로 변경 가능)
          this.stompClient.send('/pub/reserve/status', {}, JSON.stringify({ reserveIdx }));
        }
      },
    },
    beforeUnmount() {
      // 컴포넌트가 파괴되기 전에 WebSocket 연결 해제
      if (this.stompClient && this.stompClient.connected) {
        this.stompClient.disconnect(() => {
          console.log('WebSocket 연결 해제됨');
        });
      }
    }
  };
  </script>
  
  <style scoped>
  h2 {
    color: #333;
  }
  h3 {
    color: #555;
    margin-top: 20px;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  li {
    background: #f4f4f4;
    margin-bottom: 10px;
    padding: 10px;
    border-radius: 5px;
  }
  button {
    margin-top: 20px;
    padding: 10px 20px;
    font-size: 16px;
    cursor: pointer;
  }
  </style>
