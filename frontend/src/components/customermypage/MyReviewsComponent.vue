<template>
  <div class="reviews-container">
    <h2>내 리뷰</h2>
    <div v-if="reviews.length > 0" class="reviews-list">
      <div v-for="storeReview in reviews" :key="storeReview.reviewIdx" class="storeReview-item">
        <div v-if="storeReview.searchReviewImageResList.length > 0" class="storeReview-images">
          <img
            v-for="image in storeReview.searchReviewImageResList"
            :key="image.reviewImageIdx"
            :src="image.imageUrl"
            :alt="storeReview.reviewTitle"
            class="storeReview-image"
          />
        </div>
        <h3 class="storeReview-title">{{ storeReview.reviewTitle }}</h3>
        <p class="storeReview-content">{{ storeReview.reviewContent }}</p>
        <p class="storeReview-rating">Rating: {{ storeReview.rating }}</p>
       
      </div>
    </div>
    <div v-else>
      <p>No reviews available</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'MyReviewsComponent',
  data() {
    return {
      reviews: []
    };
  },
  mounted() {
    this.fetchReviews();
  },
  methods: {
    async fetchReviews(page = 0) {
      try {
        const apiUrl = 'http://localhost:8080/api/v1/storeReview/search-customer';
        const response = await axios.get(apiUrl, {
          params: {
            page: page,
            size: 5 
          },
          headers: {
            'Content-Type': 'application/json'
          }
        });
        this.reviews = response.data.result.content;
      } catch (error) {
        console.error('Error fetching the reviews', error);
      }
    }
  }
};
</script>

<style scoped>
.reviews-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.storeReview-item {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 5px;
  background-color: #fafafa;
}

.storeReview-title {
  font-size: 1.5em;
  margin-bottom: 10px;
}

.storeReview-content {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.storeReview-rating {
  font-size: 1em;
  font-weight: bold;
  margin-bottom: 10px;
}

.storeReview-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.storeReview-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 5px;
  object-fit: cover;
}
</style>
