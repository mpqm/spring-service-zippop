import { defineStore } from 'pinia';
import axios from 'axios';
import { backend } from '@/env';

export const useOrdersStore = defineStore('orders', {
    state: () => ({
        paymentData: {}
    }),
    persist: { storage: sessionStorage },
    actions: {
        async verify(imp_uid, flag) {
            try {
                const res = await axios.get(
                    `${backend}/orders/verify?impUid=${imp_uid}&flag=${flag}`,
                    { withCredentials: true }
                );
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },
        async setPaymentData(paymentData) {
            this.paymentData = paymentData;
        }
    },
},);