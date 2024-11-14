<template>
    <div class="review-list-item">
      <div class="review-info1">
        <p class="t1">{{ review.reviewTitle }}</p>
        <p class="t2">{{ review.reviewContent }}</p>
      </div>
      <div class="review-info2">
        <div class = "user-info">
          <p class="t3">{{ review.customerName }}/</p>
          <p class="t3">{{ review.customerEmail }}</p>
        </div>
        <div>
          <img class="stock-img" src="../../assets/img/star-fill.png" alt="" />&nbsp;{{ review.reviewRating }}
        </div>
        
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps } from "vue";
  
defineProps({
    review: Object,
  });
  </script>
  
  <style scoped>

  .review-list-item {
    display: flex;
    flex-direction: column;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .review-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
    margin-right: 12px;
  }
  
  .review-info1 {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }
  
  .review-info2 {
    display: flex;
    justify-content: space-between;
    margin: 0;
    font-size: 0.9rem;
    color: #666;
  }
  .user-info {
    display: flex;
  }
  
  .t1 {
    margin: 0;
    font-size: 1.2rem;
    font-weight: bold;
  }
  
  .t2 {
    margin: 0;
    font-size: 1.1rem;
    color: #333;
  }
  
  .t3 {
    margin: 0;
    font-size: 0.9rem;
    color: #666;
  }
  
  .review-content {
    font-size: 0.9rem;
    color: #666;
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
    text-decoration: none;
  }
  
  .coin-img {
    object-fit: cover;
    width: auto;
    height: 20px;
    vertical-align: middle;
  }
  
  .stock-img {
    object-fit: cover;
    width: auto;
    height: 20px;
    vertical-align: middle;
  }
  </style>
  