<template>
    <div class="settlement-list-item">
        <p class="t1"> 정산 날짜 : {{ settlement.settlementDate }} </p>
        <p class="t1"> 일별 수익 : {{ settlement.totalRevenue }} </p>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from "vue";

  // props 정의(settlement, showControl)
  defineProps({
    settlement: Object,
  });
  </script>
  
  <style scoped>
  .settlement-list-item {
      display: flex;
      align-items: center;
    justify-content: space-between;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .settlement-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
    margin-right: 12px;
  }
  
  .settlement-info1 {
    display: flex;
    align-items: center;
  }
  
  .settlement-info2 {
    padding: 10px;
    justify-content: space-between;
    margin: 0;
    font-size: 0.9rem;
    color: #666;
  }
  
  .t1 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: bold;
  }
  
  .t2 {
    margin: 0;
    display: flex;
    align-items: center;
  }
  
  .t3 {
    margin: 0;
    font-size: 0.9rem;
    color: #666;
  }
  
  .like-img {
    object-fit: cover;
    width: auto;
    height: 20px;
    padding: 5px;
    vertical-align: middle;
  }
  
  .people-img {
    object-fit: cover;
    width: auto;
    height: 30px;
    vertical-align: middle;
  }
  
  .btn-container {
    margin-right: 0;
    margin-left: auto;
    display: flex;
    gap: 20px;
  }
  
  .ud-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
  }
  
  .normal-btn {
    display: flex;
    text-align: center;
    width: 100%;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
    align-items: center;
    justify-content: center;
  }
  </style>