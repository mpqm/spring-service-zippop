<template>
  <div>
    <h2>팝업굿즈 거래내역</h2>
    <table>
      <thead>
        <tr>
          <th>주문 번호</th>
          <th>결제 UID</th>
          <th>사용된 포인트</th>
          <th>총 가격</th>
          <th>주문 상태</th>
          <th>배송 비용</th>
          <th>주문 날짜</th>
          <th>업데이트 날짜</th>
          <th>상세 내역</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="orders.length === 0">
          <td colspan="9">거래 내역이 없습니다.</td>
        </tr>
        <tr v-for="order in orders" :key="order.customerOrdersIdx">
          <td>{{ order.customerOrdersIdx }}</td>
          <td>{{ order.impUid }}</td>
          <td>{{ order.usedPoint }}</td>
          <td>{{ order.totalPrice }}</td>
          <td>{{ order.orderState }}</td>
          <td>{{ order.deliveryCost }}</td>
          <td>{{ formatDate(order.createdAt) }}</td>
          <td>{{ formatDate(order.updatedAt) }}</td>
          <td>
            <ul>
              <li v-for="detail in order.getCustomerOrdersDetailResList" :key="detail.id">
                {{ detail.info }} <!-- Replace with actual detail fields -->
              </li>
            </ul>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'PopupGoodsComponent',
  data() {
    return {
      orders: []
    };
  },
  created() {
    this.fetchOrders();
  },
  methods: {
    async fetchOrders() {
      try {
        const response = await axios.get('/api/v1/orders/search-customer', { withCredentials: true });
        this.orders = response.data.result; // 서버에서 받은 데이터를 orders에 저장
      } catch (error) {
        console.error('Failed to fetch orders:', error);
      }
    },
    formatDate(dateString) {
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    }
  }
};
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
}

th {
  background-color: #f2f2f2;
  text-align: left;
}

td[colspan="9"] {
  text-align: center;
  color: #888;
  font-style: italic;
}
</style>
