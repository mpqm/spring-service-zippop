<template>
    <div id="app-body">
        <div data-testid="login-page" class="login-page" data-v-ac2db00e="">
            <h2 data-v-ac2db00e="">ZIPPOP 로그인</h2>
            <div data-v-ac2db00e="" class="card">
                <form data-v-b7b1ce04="" data-v-ac2db00e="" @submit.prevent="login" autocomplete="off" class="">
                    <div data-v-b7b1ce04="" class="form-row login-form">
                        <div data-v-b7b1ce04="" class="col-12">
                            <fieldset data-v-b7b1ce04="" class="form-group" id="__BVID__74">
                                <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__74__BV_label_"> 이메일 </legend>
                                <div>
                                    <input data-v-b7b1ce04="" v-model="member.email" name="email" type="email" placeholder="example@soomgo.com" autocomplete="off" class="text-input form-control is-valid" data-testid="login-email" x-autocompletetype="off" autocorrect="off" spellcheck="false" autocapitalize="off" data-vv-validate-on="blur" id="__BVID__75" data-gtm-form-interact-field-id="0" aria-required="true" aria-invalid="false" />
                                    <div data-v-b7b1ce04="" class="invalid-feedback"></div>
                                </div>
                            </fieldset>
                        </div>
                        <div data-v-b7b1ce04="" class="col-12">
                            <fieldset data-v-b7b1ce04="" class="form-group password" id="__BVID__76">
                                <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__76__BV_label_"> 비밀번호 </legend>
                                <div>
                                    <input v-model="member.password" data-v-b7b1ce04="" name="password" type="password" placeholder="비밀번호를 입력해 주세요." autocomplete="off" class="text-input form-control is-valid" data-testid="login-password" x-autocompletetype="off" autocorrect="off" spellcheck="false" autocapitalize="off" data-vv-validate-on="blur" aria-invalid="true" id="__BVID__77" aria-required="true" />
                                </div>
                            </fieldset>
                        </div>
                        <div data-v-b7b1ce04="" class="col-12">
                            <button data-v-b7b1ce04="" data-testid="btn-login" type="submit" class="btn btn-login btn-primary">
                                <span data-v-b7b1ce04="">이메일 로그인</span>
                                <span data-v-b7b1ce04="" style="display: none">
                                    <div data-v-fc3fcce8="" data-v-b7b1ce04="" class="">
                                        <div data-v-fc3fcce8="" class="indicator-body email_login_btn"></div>
                                        <p data-v-fc3fcce8="" style="display: none"></p>
                                    </div>
                                </span>
                            </button>
                        </div>
                        <div data-v-b7b1ce04="" class="col-12">
                            <a href="/signup/customer" data-v-b7b1ce04="" data-testid="btn-login" class="btn btn-login btn-primary">
                                <span data-v-b7b1ce04="">회원가입</span>
                                <span data-v-b7b1ce04="" style="display: none">
                                    <div data-v-fc3fcce8="" data-v-b7b1ce04="" class="">
                                        <div data-v-fc3fcce8="" class="indicator-body email_login_btn"></div>
                                        <p data-v-fc3fcce8="" style="display: none"></p>
                                    </div>
                                </span>
                            </a>
                        </div>
                        <div data-v-b7b1ce04="" class="col-12">
                            <button data-v-3998184b="" data-v-b7b1ce04="" type="button" class="btn kakao-login-btn btn-secondary">
                                <img data-v-3998184b="" src="https://assets.cdn.soomgo.com/icons/icon-login-kakaotalk-btn.svg" alt="카카오 로그인" />
                                    <span data-v-3998184b="">카카오로 시작</span>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</template>


<script>
import { mapStores } from 'pinia'
import { useMemberStore } from '@/stores/useMemberStore';

export default {
    name: "LoginComponent",
    data() {
        return { 
            member: { 
                email: null, 
                password: null 
            } 
        };
    },
    computed: { ...mapStores(useMemberStore) },
    methods: {
        login() {
            const result = this.memberStore.login(this.member);
            if (result) { this.$router.push("/") }
        }
    }
}
</script>

<style scoped>

.email_login_btn{
    width: 24px;
    height: 24px;
    border-width: 0.25rem;
    border-style: solid;
    border-color: rgb(255, 255, 255) rgba(255, 255, 255, 0.2)
    rgba(255, 255, 255, 0.2);
    border-image: initial;
}
#app {
    -webkit-text-size-adjust: 100%;
    -moz-text-size-adjust: 100%;
    text-size-adjust: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    opacity: 1;
    min-height: 100%
}

#app #app-body,
#app #app-header {
    opacity: 1;
    transition: opacity .3s ease-in
}

#app #app-sticky-nav {
    position: sticky;
    top: 3.625rem;
    left: 0;
    right: 0;
    z-index: 2;
    transform: translateY(-100%)
}

@media (min-width: 992px) {
    #app #app-sticky-nav {
        top: 4.5rem
    }
}

.has-lnb #app #app-sticky-nav {
    position: relative;
    top: 6.375rem
}

#app #app-sticky-nav>* {
    visibility: hidden;
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    transition: all .3s ease
}

#app #app-sticky-nav>.show {
    visibility: initial;
    height: auto;
    transform: translateY(100%)
}

#app #app-body {
    height: 100%;
    position: relative;
    flex-grow: 1
}

#app[data-is-loaded=true],
#app[data-is-loaded=true] #app-body,
#app[data-is-loaded=true] #app-header,
#app[data-is-loaded=true] #app-sticky-nav {
    opacity: 1
}

.login #app,
.sign-up #app {
    background: #fafafa
}

.a11y-hidden {
    position: absolute;
    width: .0625rem;
    height: .0625rem;
    margin: -.0625rem;
    overflow: hidden;
    -webkit-clip-path: polygon(0 0, 0 0, 0 0);
    clip-path: polygon(0 0, 0 0, 0 0)
}

.login-page[data-v-ac2db00e] {
    padding: 5rem 0;
    background-color: #fafafa
}

.login-page h2[data-v-ac2db00e] {
    margin-bottom: 2.5rem;
    font-size: 2.125rem;
    font-weight: 500;
    line-height: normal;
    text-align: center;
    letter-spacing: normal
}

@media (max-width: 991.98px) {
    .login-page h2[data-v-ac2db00e] {
        padding-left: 1rem;
        font-size: 1.75rem;
        text-align: left
    }
}

@media (max-width: 991.98px) {
    .login-page[data-v-ac2db00e] {
        padding-top: 2.5rem;
        background-color: #fff
    }
}

.card[data-v-ac2db00e] {
    width: 100%;
    max-width: 26.5rem;
    padding: 2.5rem;
    margin: 0 auto;
    border: .0625rem solid #f2f2f2;
    border-radius: 8px;
    box-shadow: none
}

@media (max-width: 991.98px) {
    .card[data-v-ac2db00e] {
        width: 100%;
        padding: 0 1rem;
        border: none
    }
}

.login-form .card[data-v-b7b1ce04] {
    padding: 2.5rem
}

.login-form[data-v-b7b1ce04] .col-form-label {
    padding-bottom: .375rem;
    font-size: 1rem;
    font-weight: 500
}

.login-form .form-group[data-v-b7b1ce04] {
    margin-bottom: 1.5rem
}

.login-form .reset-password-or-signup-wrapper[data-v-b7b1ce04] {
    display: flex
}

.login-form .reset-password-or-signup-wrapper>div[data-v-b7b1ce04] {
    display: flex;
    margin: 0 auto 2rem;
    font-size: .875rem
}

.login-form .reset-password-or-signup-wrapper>div>.line[data-v-b7b1ce04] {
    width: .0625rem;
    height: .75rem;
    margin: auto .75rem;
    background-color: #e1e1e1
}

.login-form .reset-password-or-signup-wrapper>div>a[data-v-b7b1ce04] {
    padding: .375rem .5rem;
    font-weight: 400;
    color: #737373
}

.login-form .recent-login[data-v-b7b1ce04] {
    font-size: .75rem;
    font-weight: 400
}

.recent-login[data-v-a5692ec4] {
    color: #737373;
    text-align: center;
}

.login-form .btn[data-v-b7b1ce04] {
    width: 100%;
    margin-bottom: .75rem;
    font-weight: 400
}

.login-form .btn.kakao-login-btn[data-v-b7b1ce04] {
}

.login-form .btn.fb-login-btn[data-v-b7b1ce04] {
    margin-bottom: 0
}

.login-form .btn span[data-v-b7b1ce04] {
    color: #fff
}

.login-form .btn[data-v-b7b1ce04] .indicator-body {
    margin: 0 auto
}

.login-form .text-input[data-v-b7b1ce04] {
    padding: .75rem;
    border: .0625rem solid #e1e1e1;
    border-radius: 4px
}

.login-form .text-input[data-v-b7b1ce04]::-moz-placeholder {
    font-weight: 400;
    line-height: normal;
    color: #b5b5b5;
    letter-spacing: normal
}

.login-form .text-input[data-v-b7b1ce04]::placeholder {
    font-weight: 400;
    line-height: normal;
    color: #b5b5b5;
    letter-spacing: normal
}

.login-form .text-input.invalid[data-v-b7b1ce04] {
    border: .0625rem solid #fa5963
}

button.btn[data-v-3998184b] {
    color: #323232;
    background-color: #fee500;
    border-color: #fee500;
    color: rgba(0, 0, 0, .85);
    width: 100%;
}

.naver-id-login.btn[data-v-d2f0a4f0] {
    background-color: #03c75a;
    border-color: #03c75a;
    color: #fff;
    width: 100%;
}

.login-form .text-input[data-v-b7b1ce04] {
    padding: .75rem;
    border: .0625rem solid #e1e1e1;
    border-radius: 4px;
}

.form-control.is-valid:focus,
.was-validated .form-control:valid:focus {
    border-color: #00c7ae;
    box-shadow: 0 0 0 .2rem rgba(0, 199, 174, .25);
}
</style>