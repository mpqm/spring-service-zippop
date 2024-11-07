<template>
    <main class="css-rqsb1k eqpswao0">
        <div class="css-decb2f e1m10pgf0">
            <div class="e49cm070 css-y65al5">
                <h1 class="css-2nruhc e49cm071">ZIPPOP 회원가입</h1>
                <form @submit.prevent="handleSignup" autocomplete="off" class="css-ms5z5x e11vcsx40">
                    <!-- 이메일 -->
                    <div class="css-0">
                        <label for="email" class="css-g3x1x2 et8ys2u0">이메일</label>
                        <div class="css-hyxlzm et8ys2u1">
                            <input v-model="member.email" id="email" class="css-sn3hoi et8ys2u2" placeholder="example@zippop.com" maxlength="30" name="email" >
                        </div>
                    </div>
                    <!-- 비밀번호 -->
                    <div class="css-0">
                        <label for="password" class="css-g3x1x2 et8ys2u0">비밀번호</label>
                        <div class="css-hyxlzm et8ys2u1">
                            <input v-model="member.password" id="password" class="css-sn3hoi et8ys2u2" placeholder="비밀번호를 입력해주세요" name="password" type="password" >
                        </div>
                    </div>
                    <!-- 이름 -->
                    <div class="css-0">
                        <label for="name" class="css-g3x1x2 et8ys2u0">회원 이름</label>
                        <div class="css-hyxlzm et8ys2u1">
                            <input  v-model="member.name" autofocus="" id="name" class="css-sn3hoi et8ys2u2" placeholder="이름(실명)을 입력해주세요" maxlength="7" name="name" type="text">
                        </div>
                    </div>
                    <!-- 휴대폰 번호 -->
                    <div class="css-0">
                        <label for="phone_number" class="css-g3x1x2 et8ys2u0">휴대폰 번호</label>
                        <div class="css-hyxlzm et8ys2u1">
                            <input v-model="member.phone_number" id="phone_number" class="css-sn3hoi et8ys2u2" placeholder="010-1234-5678" maxlength="13" name="phone_number" >
                        </div>
                    </div>
                    <!-- 주소 -->
                    <div class="css-0">
                        <label for="address" class="css-g3x1x2 et8ys2u0">주소</label>
                        <div class="css-hyxlzm et8ys2u1">
                            <input v-model="member.address" id="address" class="css-sn3hoi et8ys2u2" placeholder="주소를 입력해주세요" maxlength="100" name="address" >
                        </div>
                    </div>
                        
                    <!-- 회원가입 버튼 -->
                    <div class="css-yc1buc">
                        <button type="submit" class="css-1r1fbe7">회원가입</button>
                    </div>
                    <!-- info -->
                    <div class="e1gu70o50 css-sc8ysd">
                        <div class="css-gc5ngv e1gu70o51">
                            <div class="css-17jw9e6 e1gu70o54">타인 명의로 가입 시 계정이 정지되고 재가입이 불가능합니다.</div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </main>
</template>

<script>
import { mapStores } from 'pinia'
import { useMemberStore } from '@/stores/useMemberStore';

export default {
    name: "CustomerSignupComponent",
    data() {
        return { 
            member: {
                role: 'ROLE_USER',
                name: '',       // 이름
                email: '',      // 이메일
                password: '',   // 비밀번호
                address: '',    // 주소
                phone_number: ''// 휴대폰 번호
            }
        };
    },
    computed: { ...mapStores(useMemberStore) },
    methods: {
        handleSignup() {
            const result = this.memberStore.signup(this.member);
            if(result) { this.$router.push("/login")}
        }
    }
}
</script>



<style scoped>
.css-wbvail {
    min-height: 300px;
}

.css-decb2f {
    position: relative;
    max-width: calc(420px + 32px);
    margin: 0 auto;
    padding: 0 16px;
    min-height: 300px;
}

@media (max-width: 767px) {
    .css-decb2f {
        max-width: 100%;
    }
}

.css-fepdn4 {
    padding-top: 40px;
}

@media (min-width: 768px) {
    .css-fepdn4 {
        gap: 40px;
        padding-top: 80px;
        padding-bottom: 80px;
    }
}

.css-y65al5 {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-flex-direction: column;
    -ms-flex-direction: column;
    flex-direction: column;
    gap: 40px;
    -webkit-align-items: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    padding-top: 40px;
}

@media (min-width: 768px) {
    .css-y65al5 {
        gap: 40px;
        padding-top: 80px;
        padding-bottom: 80px;
    }
}

.css-2nruhc {
    color: #323232;
    font-size: 28px;
    font-weight: 500;
    -webkit-align-self: flex-start;
    -ms-flex-item-align: flex-start;
    align-self: flex-start;
}

@media (min-width: 768px) {
    .css-2nruhc {
        -webkit-align-self: center;
        -ms-flex-item-align: center;
        align-self: center;
        font-size: 34px;
    }
}

.css-ms5z5x {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-flex-direction: column;
    -ms-flex-direction: column;
    flex-direction: column;
    gap: 24px;
    width: 100%;
    background-color: rgba(255, 255, 255, 1);
    padding-bottom: 80px;
}

@media (min-width: 768px) {
    .css-ms5z5x {
        padding: 40px;
        border-radius: 8px;
        border: 1px solid #F2F2F2;
    }
}

.css-g3x1x2 {
    display: block;
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 8px;
}

.css-hyxlzm {
    position: relative;
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
}

.css-sn3hoi {
    padding: 12px;
    border-radius: 4px;
    border: solid 1px #E1E1E1;
    color: #323232;
    font-size: 16px;
    width: 100%;
    outline: none;
    height: 48px;
    resize: none;
}

.css-sn3hoi::-webkit-input-placeholder {
    color: #B5B5B5;
}

.css-sn3hoi::-moz-placeholder {
    color: #B5B5B5;
}

.css-sn3hoi:-ms-input-placeholder {
    color: #B5B5B5;
}

.css-sn3hoi::placeholder {
    color: #B5B5B5;
}

.css-sn3hoi:focus {
    border-color: #00C7AE;
}

.css-1yuhvjn {
    margin-top: 16px;
}

.css-pqvzl5 {
    padding: 16px;
    border-radius: 8px;
    background-color: #F2F2F2;
    font-size: 14px;
    margin-top: 16px;
}

.css-sc8ysd {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-flex-direction: column;
    -ms-flex-direction: column;
    flex-direction: column;
    gap: 4px;
    padding: 16px;
    border-radius: 8px;
    background-color: #F2F2F2;
    font-size: 14px;
    margin-top: 16px;
}

.css-gc5ngv {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    width: 100%;
    color: #323232;
}

.css-17jw9e6 {
    width: 100%;
    color: #323232;
}

.css-1xsbg0a {
    padding-right: 50px;
}

.css-ix093q {
    padding: 12px;
    border-radius: 4px;
    border: solid 1px #E1E1E1;
    color: #323232;
    font-size: 16px;
    width: 100%;
    outline: none;
    height: 48px;
    resize: none;
    padding-right: 50px;
}

.css-ix093q::-webkit-input-placeholder {
    color: #B5B5B5;
}

.css-ix093q::-moz-placeholder {
    color: #B5B5B5;
}

.css-ix093q:-ms-input-placeholder {
    color: #B5B5B5;
}

.css-ix093q::placeholder {
    color: #B5B5B5;
}

.css-ix093q:focus {
    border-color: #00C7AE;
}

.css-h2bf0h {
    border-bottom: 1px solid #E1E1E1;
    padding: 16px 0;
}

.css-h2bf0h span {
    font-weight: 500;
    color: #323232;
    font-size: 16px;
}

.css-1wvug9p {
    border-bottom: 1px solid #E1E1E1;
    padding: 16px 0;
}

.css-1wvug9p span {
    font-weight: 500;
    color: #323232;
    font-size: 16px;
}

.css-1netp80 {
    padding: 0;
    border-bottom: 1px solid #E1E1E1;
    padding: 16px 0;
}

.css-1netp80 span {
    font-weight: 500;
    color: #323232;
    font-size: 16px;
}

.css-1r86ifp {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    cursor: pointer;
    -webkit-align-items: center;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -webkit-justify-content: space-between;
    justify-content: space-between;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}

.css-44jj4s {
    position: absolute;
    width: 1px;
    height: 1px;
    margin: -1px;
    overflow: hidden;
    -webkit-clip-path: polygon(0 0, 0 0, 0 0);
    clip-path: polygon(0 0, 0 0, 0 0);
}

.css-zg1vud {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-flex-direction: column;
    -ms-flex-direction: column;
    flex-direction: column;
    -webkit-box-pack: center;
    -ms-flex-pack: center;
    -webkit-justify-content: center;
    justify-content: center;
}

.css-142vh7v {
    font-size: 14px;
    font-weight: 400;
    color: #737373;
}

.css-1mc2zzi {
    width: 24px;
    height: 24px;
    overflow: hidden;
    color: #E1E1E1;
}

.css-1is0qkf {
    padding: 20px 0;
}

.css-wsxn8z+.css-wsxn8z {
    padding-top: 20px;
}

.css-wsxn8z a {
    -webkit-text-decoration: underline;
    text-decoration: underline;
}

.css-wsxn8z .term-required {
    font-weight: 400;
    padding-left: 4px;
    color: #B5B5B5;
}

.css-l5t2cr {
    padding: 0;
}

.css-yc1buc {
    display: -webkit-box;
    display: -webkit-flex;
    display: -ms-flexbox;
    display: flex;
    -webkit-flex-direction: column;
    -ms-flex-direction: column;
    flex-direction: column;
    gap: 12px;
}


.css-1r1fbe7 {
    display: inline-block;
    padding: 0;
    font-weight: 500;
    text-align: center;
    border-width: 1px;
    border-style: solid;
    background-color: transparent;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    transition-property: color, border-color, background-color;
    transition-duration: 0.15s;
    transition-timing-function: ease-in-out;
    color: #FFFFFF;
    border-color: #00C7AE;
    background-color: #00C7AE;
    padding: 11px 12px;
    font-size: 16px;
    border-radius: 4px;
    width: initial;
}

.css-1r1fbe7:hover {
    background-color: #09A08E;
    will-change: background-color;
}

.css-1r1fbe7:disabled,
.css-1r1fbe7[disabled] {
    background-color: #B5B5B5;
    border-color: #B5B5B5;
    cursor: not-allowed;
}

.css-9obdx3 {
    display: inline-block;
    padding: 0;
    font-weight: 500;
    text-align: center;
    border-width: 1px;
    border-style: solid;
    background-color: transparent;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    transition-property: color, border-color, background-color;
    transition-duration: 0.15s;
    transition-timing-function: ease-in-out;
    color: #737373;
    border-color: transparent;
    background-color: transparent;
    padding: 11px 12px;
    font-size: 16px;
    border-radius: 4px;
    width: initial;
}

.css-1bhzmwp {
    padding: 12px;
    border-radius: 4px;
    border: 1px solid rgb(250, 89, 99);
    color: rgb(50, 50, 50);
    font-size: 16px;
    width: 100%;
    outline: none;
    height: 48px;
    resize: none;
}

@media (min-width: 768px) {
    .css-rqsb1k {
        background-color: #FAFAFA;
    }
}

.css-1ds1hu0 {
    font-size: 14px;
    font-weight: 400;
    color: rgb(250, 89, 99);
}
</style>