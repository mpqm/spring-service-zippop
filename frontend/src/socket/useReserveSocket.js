import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { ref } from 'vue';

let stompClient;

export const reserveStatusRef = ref({
  workingTotal: 0,
  waitingTotal: 0,
  statusMessage: '',
});

// WebSocket 연결
export const connectWebSocket = async (onMessageCallback) => {
  return new Promise((resolve, reject) => {
    const socket = new SockJS('http://localhost:8080/ws');
    stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      onConnect: () => {
        console.log('WebSocket connected');
        stompClient.subscribe('/sub/reserve-status', (message) => {
          const payload = JSON.parse(message.body);
          onMessageCallback(payload); // 메시지 처리
        });
        resolve();
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
      },
      onWebSocketError: (error) => {
        console.error('WebSocket error:', error);
        reject(error);
      },
    });
    stompClient.activate();
  });
};

// WebSocket 연결 해제
export const disconnectWebSocket = () => {
  if (stompClient) stompClient.deactivate();
};

// 상태 요청 전송
export const sendStatusRequest = async (reserveIdx) => {
  return new Promise((resolve, reject) => {
    if (stompClient && stompClient.connected) {
      stompClient.publish({
        destination: '/pub/status', // 서버로 메시지 발행
        body: JSON.stringify(reserveIdx),
      });
      resolve();
    } else {
      reject(new Error('WebSocket is not connected'));
    }
  });
};
