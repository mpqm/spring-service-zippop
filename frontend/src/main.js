import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import { createPinia } from "pinia";
import piniaPersistedstate from "pinia-plugin-persistedstate";
import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";
import axios from 'axios'
import { useAuthStore } from '@/stores/authStore';
import { Icon } from '@iconify/vue'
import AppEmptyState from '@/components/AppEmptyState.vue';
import { isLoginRequiredError, redirectToLogin } from '@/utils/errorHandling';

const pinia = createPinia();
pinia.use(piniaPersistedstate);
const app = createApp(App);
// eslint-disable-next-line vue/multi-word-component-names
app.component('Icon', Icon);
app.component('AppEmptyState', AppEmptyState);
app.use(pinia);
app.use(Toast,
    {
        timeout: 2000,
        enter: "fade-enter-active",
        leave: "Vue-Toastification__bounce-leave-active",
        move: "fade-move",
        position: "bottom-right"
    });
app.use(router);

// Axios 인터셉터 설정
axios.interceptors.response.use(
    response => { return response; },
    async error => {
        if (error.response) {
            const status = error.response.status;
            const authStore = useAuthStore();
            const requestUrl = error.config?.url || "";
            const isLoginRequest = requestUrl.includes('/auth/login');
            if (isLoginRequiredError(error) && !isLoginRequest) {
                await redirectToLogin(router, authStore);
            } else if(status === 404){
                router.push("/error");
            } else if(status >= 500){
                router.push("/error");
            }
        }
        return Promise.reject(error);
    }
);

app.mount("#app");
