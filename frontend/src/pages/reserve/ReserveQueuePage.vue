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

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useAuthStore } from "@/stores/useAuthStore";

const stompClient = ref(null);
const statusMessages = ref([]); // 상태 메시지를 저장할 배열

onMounted(() => {
  connectWebSocket();
});



// 소켓 연결
const connectWebSocket = () => {
  const socket = new SockJS("http://localhost:8080/ws"); // WebSocket 엔드포인트
  stompClient.value = Stomp.over(socket);
  stompClient.value.connect(
    {},
    (frame) => {
      console.log("소켓 연결: " + frame);

      // 사용자 이메일 구독
      const userEmail = useAuthStore().userInfo.email;
      stompClient.value.subscribe(`/user/${userEmail}/reserve/status`, (message) => {
        console.log("메시지 응답 ", message);
        if (message.body) {
          try {
            const data = JSON.parse(message.body);
            statusMessages.value.push({ workingTotal: data.workingTotal, waitingTotal: data.waitingTotal, statusMessage: data.statusMessage, });
          } catch (error) {
            console.error("메시지 파싱 실패", error);
          }
        } else {
          console.log("메시지 응답 없음");
        }
      });
    },
    (error) => {
      console.error("소켓 연결 실패: ", error);
      // 5초 후 재연결 시도
      setTimeout(() => { connectWebSocket(); }, 5000);
    }
  );
};

const sendStatusRequest = () => {
  if (stompClient.value && stompClient.value.connected) {
    const reserveIdx = 14; // 예시 예약 ID (동적으로 변경 가능)
    stompClient.value.send(
      "/pub/reserve/status",
      {},
      JSON.stringify({ reserveIdx })
    );
  }
};

onBeforeUnmount(() => {
  if (stompClient.value && stompClient.value.connected) {
    stompClient.value.disconnect(() => {
      console.log("WebSocket 연결 해제됨");
    });
  }
});
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
