<template>
  <div class="container">
    <div class="status-box">
      <p><span>{{ statusMessage }}</span></p>
      <button class="cancel-button" @click="cancel">예약 취소</button>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useAuthStore } from "@/stores/useAuthStore";
import { useRoute, useRouter } from "vue-router";
import { useReserveStore } from "@/stores/useReserveStore";
import { useToast } from "vue-toastification";
const reserveStore = useReserveStore();
const route = useRoute();
const toast = useToast();
const router = useRouter();
const stompClient = ref(null);
const authStore = useAuthStore();

const workingTotal = ref(0); // 작업 중인 예약 수
const waitingTotal = ref(0); // 대기 중인 예약 수
const statusMessage = ref(""); // 상태 메시지

let statusInterval = null; // Interval ID 저장
// Vue 컴포넌트 라이프사이클
onMounted(async() => {
  await isUserLoggedIn()
  await enroll()
  connectWebSocket();
  // 3c초마다 상태 요청
  statusInterval = setInterval(() => {
    sendInitialStatusRequest();
  }, 3000);
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});

const isUserLoggedIn = async() => {
  if(authStore.isLoggedIn) {
    return;
  } else {
    router.go(-1);
    toast.error("로그인이 필요합니다.");
  }
}

const enroll = async() => {
  
  const res = await reserveStore.enroll(route.params.reserveIdx);
  if(!res.success) {
    router.push("/")
  }
}

const cancel = async() => {
  const res = await reserveStore.cancel(route.params.reserveIdx);
  if(res.success) {
    router.go(-1)
    toast.success(res.message)
  } else {
    router.go(-1)
    toast.error(res.message)
  }
  disconnectWebSocket();
  clearInterval(statusInterval);
}

// WebSocket 연결
const connectWebSocket = () => {
  const socket = new SockJS("http://localhost:8080/ws"); // WebSocket 엔드포인트
  stompClient.value = Stomp.over(socket);
  stompClient.value.connect(
    {},
    (frame) => {
      console.log("WebSocket 연결 성공: " + frame);
      const userEmail = useAuthStore().userInfo.email;
      stompClient.value.subscribe(`/user/${userEmail}/reserve/status`, (message) => {
      console.log("서버로부터 상태 메시지 수신: ", message);
      if (!message.body) {
        console.log("메시지 응답 없음");
      }
      try {
        const data = JSON.parse(message.body);
        waitingTotal.value = data.waitingTotal;
        workingTotal.value = data.workingTotal;
        statusMessage.value = data.statusMessage;
        if (data.access) {
            toast.success("예약 접속자로 전환되었습니다.");
            router.push(`/reserve/${route.params.reserveIdx}/orders`); // 이동할 페이지 경로
          }
      } catch (error) {
        console.error("메시지 파싱 실패", error);
      }
      });

      // 서버에 초기 상태 요청
      sendInitialStatusRequest();
    },
    (error) => {
      console.error("WebSocket 연결 실패: ", error);
      setTimeout(() => { connectWebSocket(); }, 5000);
    }
  );
};

// 초기 상태 요청
const sendInitialStatusRequest = () => {
  if (stompClient.value && stompClient.value.connected) {
    const reserveIdx = route.params.reserveIdx;
    stompClient.value.send( "/pub/reserve/status", {}, JSON.stringify({ reserveIdx }) );
  }
};

// WebSocket 연결 해제
const disconnectWebSocket = () => {
  if (stompClient.value && stompClient.value.connected) {
    stompClient.value.disconnect(() => {
      console.log("WebSocket 연결 해제됨");
    });
  }
  clearInterval(statusInterval); // Interval 해제
};


</script>

<style scoped>
/* 컨테이너를 화면 중앙에 배치 */
.container {
  display: flex;
  justify-content: center; /* 가로 중앙 정렬 */
  align-items: center; /* 세로 중앙 정렬 */
  height: 100vh; /* 화면 전체 높이 */
  background-color: #f7f7f7; /* 배경색 */
}

/* 상태 박스 디자인 */
.status-box {
  background-color: #ffffff;
  border-radius: 12px;

  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 20px 40px;
  text-align: center;
  font-family: 'Arial', sans-serif;
  color: #333;
}

/* 텍스트 스타일 */
.status-box p {
  margin: 10px 0;
  font-size: 1.2rem;
}

.status-box p span {
  font-weight: bold;
  color: #00c7ae;
}

/* 버튼 스타일 */
.cancel-button {
  background-color: #ff4d4f;
  color: #fff;
  border: none;
  padding: 10px 20px;
  font-size: 1rem;
  font-weight: bold;
  border-radius: 8px;
  cursor: pointer;
  margin-top: 20px;
  transition: background-color 0.3s ease;
}

.cancel-button:hover {
  background-color: #d43f3a; /* 버튼 호버 색상 */
}

.cancel-button:active {
  background-color: #c12e2a; /* 버튼 활성화 색상 */
}
</style>
