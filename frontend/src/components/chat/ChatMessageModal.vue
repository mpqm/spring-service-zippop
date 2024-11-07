<template>
    <div class="modal-overlay" @click.self="leaveRoom">
      <div class="modal-content">
        <div id="chat-room">
          <h2 id="room-id">{{ roomName }}</h2>
          <ul id="message-list">
            <li v-for="message in messages" :key="message.id" :class="{'sent': message.sender === username, 'received': message.sender !== username}">
              {{ message.sender }}: {{ message.content }}
            </li>
          </ul>
          <input type="text" v-model="messageInput" id="message-input" placeholder="Type a message..." />
          <button @click="sendMessage">보내기</button>
          <button @click="leaveRoom">나가기</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  import { Stomp } from '@stomp/stompjs';
  import SockJS from 'sockjs-client';
  
  export default {
    props: ['roomName', 'username'],
    data() {
      return {
        messages: [],
        messageInput: '',
        stompClient: null
      };
    },
    created() {
      this.connect();
    },
    methods: {
      connect() {
        let socket = new SockJS("http://localhost:8080/ws");
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, this.onConnected);
      },
      onConnected() {
        this.stompClient.subscribe(`/topic/${this.roomName}`, this.onMessageReceived);
        this.addUser();
        this.fetchMessages();
      },
      fetchMessages() {
        axios.get(`http://localhost:8080/api/v1/chat/rooms/${this.roomName}/messages`, { withCredentials: true })
          .then(response => {
            if (response.data.success) {
              this.messages = response.data.result;
            } else {
              console.error('Failed to fetch messages:', response.data.message);
            }
          })
          .catch(error => {
            console.error('Error fetching messages:', error);
          });
      },
      sendMessage() {
        if (this.messageInput.trim() && this.stompClient) {
          let chatMessage = {
            sender: this.username,
            content: this.messageInput.trim(),
            type: "CHAT"
          };
          this.stompClient.send(`/app/chat.sendMessage/${this.roomName}`, {}, JSON.stringify(chatMessage));
          this.messageInput = '';
        }
      },
      onMessageReceived(payload) {
        let message = JSON.parse(payload.body);
        this.messages.push(message);
      },
      addUser() {
        if (this.stompClient) {
          let chatMessage = {
            sender: this.username,
            content: `${this.username} has joined the chat`,
            type: "JOIN"
          };
          this.stompClient.send(`/app/chat.addUser/${this.roomName}`, {}, JSON.stringify(chatMessage));
        }
      },
      leaveRoom() {
        if (this.stompClient) {
          this.stompClient.unsubscribe(`/topic/${this.roomName}`);
        }
        this.$emit('leaveRoom');
      }
    }
  };
  </script>
  
  <style scoped>
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .modal-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    max-width: 600px;
    width: 100%;
  }
  
  #chat-room {
    display: flex;
    flex-direction: column;
    width: 100%;
  }
  
  #chat-room h2 {
    background-color: #00c7ae;
    color: black;
    padding: 20px;
    margin: 0;
    text-align: center;
  }
  
  #message-list {
    flex: 1;
    padding: 10px;
    overflow-y: auto;
    max-height: 400px;
  }
  
  #message-list li {
    list-style-type: none;
    margin-bottom: 10px;
    padding: 10px;
    border-radius: 10px;
    background-color: #f9f9f9;
  }
  
  #message-list .sent {
    background-color: #dcf8c6;
    align-self: flex-end;
  }
  
  #message-list .received {
    background-color: #fff;
    align-self: flex-start;
  }
  
  #message-input {
    border: 1px solid #ccc;
    padding: 10px;
    width: calc(100% - 22px);
    /* adjust to include padding */
    border-radius: 4px;
    margin: 10px;
    box-sizing: border-box;
  }
  
  #chat-room button {
    padding: 10px;
    margin: 10px;
    border: none;
    background-color: #00c7ae;
    color: black;
    border-radius: 4px;
    cursor: pointer;
  }
  
  #chat-room button:hover {
    background-color: #00c7ae;
  }
  </style>
  