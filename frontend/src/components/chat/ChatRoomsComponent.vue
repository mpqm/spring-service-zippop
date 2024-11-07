<!-- <template>
  <div id="chat-rooms">
    <h2>문의 내역</h2>
    <div class="content-wrapper">
      <ul id="room-list">
        <li v-for="room in rooms" :key="room.name" @click="joinRoom(room.name)">
          {{ room.name }}
        </li>
      </ul>
      <button @click="createRoom">문의하기</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      rooms: [] // rooms 변수를 배열로 초기화
    };
  },
  created() {
    this.fetchRooms();
  },
  methods: {
    fetchRooms() {
      axios.get('http://localhost:8080/api/v1/chat/rooms', { withCredentials: true })
        .then(response => {
          this.rooms = response.data.result; // 응답 데이터를 rooms에 할당
        })
        .catch(error => {
          console.error("Error fetching chat rooms:", error);
        });
    },
    joinRoom(roomName) {
      this.$emit('joinRoom', roomName);
    },
    createRoom() {
      let roomName = prompt("문의 제목을 입력하세요:");
      let companyEmail = prompt("보낼 팝업의 이메일을 입력하세요:");
      if (roomName && companyEmail) {
        axios.post('http://localhost:8080/api/v1/chat/rooms', { name: roomName, companyEmail: companyEmail }, { withCredentials: true })
          .then(response => {
            if (response.data.success) {
              this.rooms.push(response.data.result);
              this.fetchRooms();
            } else {
              console.error("Failed to create room:", response.data.message);
            }
          })
          .catch(error => {
            console.error("Error creating room:", error);
          });
      }
    }
  }
};
</script>

<style scoped>
#chat-rooms {
  width: 100%;
  max-width: 800px;
  height: 80vh;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

#chat-rooms h2 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

#room-list {
  list-style-type: none;
  padding: 0;
  max-height: 60vh;
  overflow-y: auto;
  flex-grow: 1;
}

#room-list li {
  padding: 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}

#room-list li:hover {
  background-color: #f1f1f1;
}

#chat-rooms button {
  padding: 15px 25px;
  margin-top: 20px;
  border: none;
  background-color: #00c7ae;
  color: black;
  border-radius: 4px;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.3s ease;
  align-self: flex-end;
}

#chat-rooms button:hover {
  background-color: #00b3a1;
}
</style> -->
<template>
  <div id="chat-rooms">
    <h2>문의 내역</h2>
    <div class="content-wrapper">
      <ul id="room-list">
        <li v-for="room in rooms" :key="room.name" @click="joinRoom(room.name)">
          {{ room.name }}
        </li>
      </ul>
      <button @click="createRoom">문의하기</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      rooms: [] // rooms 변수를 배열로 초기화
    };
  },
  created() {
    this.fetchRooms();
  },
  methods: {
    fetchRooms() {
      axios.get('http://localhost:8080/api/v1/chat/rooms', { withCredentials: true })
        .then(response => {
          this.rooms = response.data.result; // 응답 데이터를 rooms에 할당
        })
        .catch(error => {
          console.error("Error fetching chat rooms:", error);
        });
    },
    joinRoom(roomName) {
      this.$emit('joinRoom', roomName);
    },
    createRoom() {
      let roomName = prompt("문의 제목을 입력하세요:");
      let companyEmail = prompt("보낼 팝업의 이메일을 입력하세요:");
      if (roomName && companyEmail) {
        axios.post('http://localhost:8080/api/v1/chat/rooms', { name: roomName, companyEmail: companyEmail }, { withCredentials: true })
          .then(response => {
            if (response.data.success) {
              this.rooms.push(response.data.result);
              this.fetchRooms();
            } else {
              console.error("Failed to create room:", response.data.message);
            }
          })
          .catch(error => {
            if (error.response) {
              // 서버가 상태 코드를 반환한 경우 (응답 포함)
              console.error("Error creating room:", error.response.data);
            } else if (error.request) {
              // 요청이 전송되었으나 응답이 수신되지 않은 경우
              console.error("Error creating room:", error.request);
            } else {
              // 오류를 발생시킨 요청 설정 중 문제 발생
              console.error("Error creating room:", error.message);
            }
          });
      }
    }
  }
};
</script>

<style scoped>
#chat-rooms {
  width: 100%;
  max-width: 800px;
  height: 80vh;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

#chat-rooms h2 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

#room-list {
  list-style-type: none;
  padding: 0;
  max-height: 60vh;
  overflow-y: auto;
  flex-grow: 1;
}

#room-list li {
  padding: 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.3s ease;
}

#room-list li:hover {
  background-color: #f1f1f1;
}

#chat-rooms button {
  padding: 15px 25px;
  margin-top: 20px;
  border: none;
  background-color: #00c7ae;
  color: black;
  border-radius: 4px;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.3s ease;
  align-self: flex-end;
}

#chat-rooms button:hover {
  background-color: #00b3a1;
}
</style>

