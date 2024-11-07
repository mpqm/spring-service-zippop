<template>
  <div class="edit-profile-container">
    <h3>회원정보 수정</h3>
    <form @submit.prevent="updateProfile" class="edit-profile-form">
      <div class="form-group">
        <label for="name">이름</label>
        <input type="text" id="name" v-model="name" required />
      </div>
      <div class="form-group">
        <label for="phone">전화번호</label>
        <input type="tel" id="phone" v-model="phoneNumber" required />
      </div>
      <div class="form-group">
        <label for="address">주소</label>
        <input type="text" id="address" v-model="address" required />
      </div>
      <button type="submit" class="submit-button">수정하기</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'EditProfileComponent',
  data() {
    return {
      name: '',
      phoneNumber: '',
      address: ''
    };
  },
  methods: {
    async updateProfile() {
      try {
        const response = await axios.patch('http://localhost:8080/api/v1/member/edit-info', {
          name: this.name,
          phoneNumber: this.phoneNumber,
          address: this.address
        }, {
          withCredentials: true // 인증 정보 포함
        });
        if (response.data.success) {
          alert('회원정보가 성공적으로 수정되었습니다.');
        } else {
          alert('회원정보 수정에 실패했습니다.');
        }
      } catch (error) {
        console.error('Error updating profile:', error);
        alert('회원정보 수정 중 오류가 발생했습니다.');
      }
    }
  }
};
</script>

<style scoped>
:root {
  --font-family-sans-serif: "Noto Sans KR", "Malgun Gothic", -apple-system,
    "Segoe UI", Roboto, "Noto Sans", sans-serif, "Apple Color Emoji",
    "Segoe UI Emoji", "Segoe UI Symbol", "Noto Color Emoji";
  --font-family-monospace: SFMono-Regular, Menlo, Monaco, Consolas,
    "Liberation Mono", "Courier New", monospace;
}

body {
  font-family: var(--font-family-sans-serif);
}

.edit-profile-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: var(--font-family-sans-serif);
}

h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.edit-profile-form {
  display: flex;
  flex-direction: column;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

input:focus {
  border-color: #00c7aa;
}

.submit-button {
  padding: 10px 15px;
  background-color: #00c7aa;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-button:hover {
  background-color: #00a38a;
}
</style>
