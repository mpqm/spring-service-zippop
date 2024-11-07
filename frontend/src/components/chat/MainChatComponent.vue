<template>
  <div id="chat-app">
    <div v-if="!usernameLoading">
      <div v-if="!username">
        사용자 이름을 로드 중입니다...
      </div>
      <div v-else>
        <chat-rooms-component v-if="!currentRoom" @joinRoom="joinRoom" @createRoom="createRoom"></chat-rooms-component>
        <chat-message-modal v-else :roomName="currentRoom" :username="username" @leaveRoom="leaveRoom"></chat-message-modal>
      </div>
    </div>
  </div>
</template>

<script>

import ChatRoomsComponent from './ChatRoomsComponent.vue';
import ChatMessageModal from './ChatMessageModal.vue';

export default {
  components: {
    ChatRoomsComponent,
    ChatMessageModal
  },
  data() {
    return {
      username: '',
      currentRoom: null,
      usernameLoading: true
    };
  },
  created() {
    this.setUsernameFromToken();
  },
  methods: {
    setUsernameFromToken() {
      const utoken = this.getCookie('UTOKEN');
      if (utoken) {
        this.username = utoken.split('|')[0];
      }
      this.usernameLoading = false;
    },
    getCookie(name) {
      const value = `; ${document.cookie}`;
      const parts = value.split(`; ${name}=`);
      if (parts.length === 2) return parts.pop().split(';').shift();
    },
    joinRoom(roomName) {
      this.currentRoom = roomName;
    },
    leaveRoom() {
      this.currentRoom = null;
    }
  }
};
</script>

<style scoped>
#chat-app {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  height: 100vh;
  justify-content: center;
  background-color: #f0f0f0;
}

#chat-app > div {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

#chat-app h2 {
  margin: 0;
  padding: 20px;
  background-color: #00c7ae;
  color: black;
  text-align: center;
}
</style>
