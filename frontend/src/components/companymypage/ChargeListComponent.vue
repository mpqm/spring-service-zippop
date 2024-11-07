<template>
  <div>
    <h4>수수료 결제 내역</h4>
    <table>
      <thead>
        <tr>
          <th>주문 ID</th>
          <th>결제 UID</th>
          <th>생성 일시</th>
          <th>수정 일시</th>
          <th>상세 내역</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="orders.length === 0">
          <td colspan="5">결제 내역이 없습니다.</td>
        </tr>
        <tr v-for="order in orders" :key="order.companyOrdersIdx">
          <td>{{ order.companyOrdersIdx }}</td>
          <td>{{ order.impUid }}</td>
          <td>{{ formatDate(order.createdAt) }}</td>
          <td>{{ formatDate(order.updatedAt) }}</td>
          <td>
            <ul>
              <li v-for="detail in order.getCompanyOrdersDetailResList" :key="detail.id">
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
  name: 'ChargeListComponent',
  data() {
    return {
      orders: []
    };
  },
  methods: {
    async fetchOrders() {
      try {
        const response = await axios.get('/search-company');
        this.orders = response.data.data; // Assuming the data is in `data.data`
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    },
    formatDate(dateString) {
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' };
      return new Date(dateString).toLocaleDateString(undefined, options);
    }
  },
  mounted() {
    this.fetchOrders();
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

td[colspan="5"] {
  text-align: center;
  color: #888;
  font-style: italic;
}
</style>
