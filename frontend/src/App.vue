<template>
  <router-view></router-view>
</template>

<script setup>

</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');

body {
  font-family: 'Noto Sans KR', sans-serif;
  margin: 0;
  padding: 0;
}

.root-page {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}

.management-page {
  flex-direction: row;
  width: 65rem;
}

.two-section-container {
  margin: 1rem 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* root */
.fs17fw800 {
  margin: 0;
  font-size: 17px;
  font-weight: 800;
}

.fs15fw400{
  margin: 0;
  font-size: 15px;
  font-weight: 400;
}

.fs13fw400{
  margin: 0;
  font-size: 13px;
  font-weight: 400;
}

.fs11fw400{
  margin: 0;
  font-size: 11px;
  font-weight: 400;
}

/* 검색 컴포넌트 */
.search-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.search-input{
  border: 1px solid #00c7ae;
  border-radius: 0.25rem;
  display: flex;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  padding: 0.5rem;
  width: 30rem;
  box-sizing: border-box;
  color: #323232;
  background-color: #fff;
}

.search-input:focus {
  outline: none;
}

/* 버튼 컴포넌트 */
.btn-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.default-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  text-align: center;
  font-size: 15px;
  font-weight: 800;
  width: auto;
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

.default-btn:hover {
  opacity: 0.8;
}

.empty-string {
  text-align: center;
}

/* 리스트 컴포넌트 */
.list-container {
  width: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.default-list {
  display: flex;
  align-items: center;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.list-item-img {
  width: 70px;
  height: 40px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}

.list-item-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.list-item-info1 {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.list-item-info2 {
  display: flex;
  margin: 0;
  align-items: center;
}

.list-item-btn-container {
  margin-right: 0;
  margin-left: auto;
  display: flex;
  gap: 10px;
}

.list-item-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  text-align: center;
  width: auto;
  font-size: 15px;
  font-weight: 800;
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

/* 테이블 컴포넌트 */
.table-container {
  width: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}



.default-cell {
  display: flex;
  flex-direction: row;
  justify-content: center;
  gap: 8px;
  align-items: center;
  padding: 5px;
}

.image {
  margin: 5px 5px 0 5px;
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
}

.no-image {
  width: 50px;
  height: 50px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
}

.table-btn-container {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

.table-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  background-color: #00c7ae;
  color: white;
}

.table-btn:hover {
  background-color: #00a896;
  color: white;
}


</style>

