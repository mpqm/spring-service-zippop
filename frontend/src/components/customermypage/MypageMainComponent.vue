<!-- 
<template>
  <div id="app-body">
    <div class="mypage-container">
      <h1 class="mypage-title">마이페이지</h1>
      <div class="content-wrapper">
        <div class="profile-container underline">
          <div class="thumb">
            <div class="user-profile-picture h-100">
              <div
                :style="{ backgroundImage: `url(${profileImageUrl})` }"
                class=""
              ></div>
            </div>
          </div>
          <div class="user-info">
            <div class="user-name">
              <span class="name-length">{{ userName }}</span><span class="user-type"></span>
            </div>
            <div class="user-id">
              <img src="https://assets.cdn.soomgo.com/icons/icon-mypage-kakaotalk-signin.svg" />
              <span class="id-length">{{ userEmail }}</span>
            </div>
          </div>
          <div class="account-setting">
            <button type="button" class="btn btn-account-setting btn-secondary">계정설정</button>
          </div>
          <h5>내 포인트: {{ userPoints }}point</h5>
        </div>
        <div class="my-page-menu">
          <ul class="underline">
            <li class="main-menu">
              <h4>내 정보</h4>
              <hr>
              <router-link to="/mypage/popup">팝업굿즈 거래내역</router-link>
            </li>
          </ul>
        
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/mypage/account-edit">회원정보 수정</router-link>
            </li>
          </ul>
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/mypage/reviews">내 리뷰</router-link>
            </li>
          </ul>
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/chats">채팅</router-link>
            </li>
          </ul>
        </div>
        <div class="component-display">
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MypageMain',
  data() {
    return {
      userName: '',
      userEmail: '',
      userPoints: 0,
      profileImageUrl: ''
    };
  },
  created() {
    this.getUserProfile();
  },
  methods: {
    async getUserProfile() {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/member/profile');
        const user = response.data.result;
        this.userName = user.name;
        this.userEmail = user.email;
       
      
      } catch (error) {
        console.error('Error fetching user profile:', error);
      }
      try {
        const response = await axios.get('http://localhost:8080/api/v1/cart/points');
        const user = response.data.result; 
        this.userPoints = user.point;
      
       
      
      } catch (error) {
        console.error('Error fetching user profile:', error);
      }

    }
  }
};
</script>

<style scoped>
#app-body {
 
  justify-content: center;
  padding: 100px;
 
  background-color: #f9f9f9;
}

.mypage-container {
  display: flex;
  flex-direction: column;
 
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
}

.mypage-title {
  font-size: 24px;
  font-weight: bold;
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.profile-container {
  padding: 20px;
  border-right: 1px solid #eaeaea;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 200px; /* 고정 너비 설정 */
}

.profile-container .thumb {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 20px;
}

.profile-container .user-info {
  text-align: center;
  margin-bottom: 20px;
}

.profile-container .user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap; /* 텍스트가 한 줄로 유지되도록 설정 */
}

.profile-container .user-id {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #888888;
  white-space: nowrap; /* 텍스트가 한 줄로 유지되도록 설정 */
}

.profile-container .account-setting {
  margin-bottom: 20px;
}

.profile-container .btn {
  width: 100%;
  margin-bottom: 10px;
}

.content-wrapper {
  display: flex;
  flex-grow: 1;
  padding: 20px;
}

.my-page-menu {
  text-align: center;
  width: 200px; /* 고정 너비 설정 */
  border-right: 1px solid #eaeaea;
  padding-right: 20px;
}

.my-page-menu ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  margin-bottom: 20px;
}

.my-page-menu .main-menu {
  margin-bottom: 10px;
}

.my-page-menu .main-menu router-link {
  width: 100%;
  display: block;
  text-align: left;
  padding: 10px 15px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
  height: 40px; /* 고정 높이 설정 */
  text-decoration: none;
  color: inherit;
}

.my-page-menu .main-menu router-link:hover {
  background-color: #f0f0f0;
}

.component-display {
  flex-grow: 1;
  padding-left: 20px;
}

.user-profile-picture div[data-name=image] {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #e1e1e1;
  border: 0.0625rem solid #f2f2f2;
  border-radius: 50%;
  background-size: cover;
  background-position: 50% 50%;
}

</style>
 -->
 <template>
  <div id="app-body">
    <div class="mypage-container">
      <h1 class="mypage-title">마이페이지</h1>
      <div class="content-wrapper">
        <div class="profile-container underline">
          <div class="thumb">
            <div class="user-profile-picture h-100">
              <div
                :style="{ backgroundImage: `url(${profileImageUrl})` }"
                class=""
              ></div>
            </div>
          </div>
          <div class="user-info">
            <div class="user-name">
              <span class="name-length">{{ userName }}</span><span class="user-type"></span>
            </div>
            <div class="user-id">
              <img src="https://assets.cdn.soomgo.com/icons/icon-mypage-kakaotalk-signin.svg" />
              <span class="id-length">{{ userEmail }}</span>
            </div>
          </div>
      
          <h5>내 포인트: {{ userPoints }} point</h5>
        </div>
        <div class="my-page-menu">
          <ul class="underline">
            <li class="main-menu">
              <h4>내 정보</h4>
              <hr>
              <router-link to="/mypage/popup">팝업굿즈 거래내역</router-link>
            </li>
          </ul>
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/mypage/account-edit">회원정보 수정</router-link>
            </li>
          </ul>
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/mypage/reviews">내 리뷰</router-link>
            </li>
          </ul>
          <ul class="underline">
            <li class="main-menu">
              <router-link to="/chats">문의 내역</router-link>
            </li>
          </ul>
        </div>
        <div class="component-display">
          <router-view></router-view>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MypageMain',
  data() {
    return {
      userName: '',
      userEmail: '',
      userPoints: 0,
      profileImageUrl: ''
    };
  },
  created() {
    this.getUserProfileFromCookie();
    this.getUserProfile();
  },
  methods: {
    getUserProfileFromCookie() {
      const utoken = this.getCookie('UTOKEN');
      if (utoken) {
        const userName = utoken.split('|')[0];
        this.userName = userName;
      }
    },
    getCookie(name) {
      const value = `; ${document.cookie}`;
      const parts = value.split(`; ${name}=`);
      if (parts.length === 2) return parts.pop().split(';').shift();
    },
    async getUserProfile() {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/member/profile');
        const user = response.data.result;
        this.userEmail = user.email;
      } catch (error) {
        console.error('Error fetching user profile:', error);
      }
      try {
        const response = await axios.get('http://localhost:8080/api/v1/cart/points');
        const user = response.data.result;
        this.userPoints = user.point;
      } catch (error) {
        console.error('Error fetching user points:', error);
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

#app-body {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh; /* Viewport height */
  padding: 20px;
  background-color: #f9f9f9;
}

.mypage-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 1200px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  min-height: 80vh; /* 최소 높이 설정 */
}

.mypage-title {
  font-size: 24px;
  font-weight: bold;
  padding: 20px;
  border-bottom: 1px solid #eaeaea;
}

.content-wrapper {
  display: flex;
  flex-grow: 1;
  padding: 20px;
}

.profile-container {
  padding: 20px;
  border-right: 1px solid #eaeaea;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 200px; /* 고정 너비 설정 */
}

.profile-container .thumb {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin-bottom: 20px;
}

.profile-container .user-info {
  text-align: center;
  margin-bottom: 20px;
}

.profile-container .user-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap; /* 텍스트가 한 줄로 유지되도록 설정 */
}

.profile-container .user-id {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #888888;
  white-space: nowrap; /* 텍스트가 한 줄로 유지되도록 설정 */
}

.my-page-menu {
  text-align: center;
  width: 200px; /* 고정 너비 설정 */
  border-right: 1px solid #eaeaea;
  padding-right: 20px;
}

.my-page-menu ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  margin-bottom: 20px;
}

.my-page-menu .main-menu {
  margin-bottom: 10px;
}

.my-page-menu .main-menu router-link {
  width: 100%;
  display: block;
  text-align: left;
  padding: 10px 15px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s ease;
  box-sizing: border-box;
  height: 40px; /* 고정 높이 설정 */
  text-decoration: none;
  color: inherit;
}

.my-page-menu .main-menu router-link:hover {
  background-color: #f0f0f0;
}

.component-display {
  flex-grow: 1;
  padding-left: 20px;
}

.user-profile-picture div[data-name=image] {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #e1e1e1;
  border: 0.0625rem solid #f2f2f2;
  border-radius: 50%;
  background-size: cover;
  background-position: 50% 50%;
}
</style>
