<template>
  <div class="reviews-container">
    <h2>내 리뷰</h2>
    <div v-if="reviews.length > 0" class="reviews-list">
      <div v-for="review in reviews" :key="review.reviewIdx" class="review-item">
        <div v-if="review.getPopupReviewImageResList.length > 0" class="review-images">
          <img
            v-for="image in review.getPopupReviewImageResList"
            :key="image.reviewImageIdx"
            :src="image.imageUrl"
            :alt="review.reviewTitle"
            class="review-image"
          />
        </div>
        <h3 class="review-title">{{ review.reviewTitle }}</h3>
        <p class="review-content">{{ review.reviewContent }}</p>
        <p class="review-rating">Rating: {{ review.rating }}</p>
       
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
        const apiUrl = 'http://localhost:8080/api/v1/review/search-customer';
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

.review-item {
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 5px;
  background-color: #fafafa;
}

.review-title {
  font-size: 1.5em;
  margin-bottom: 10px;
}

.review-content {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.review-rating {
  font-size: 1em;
  font-weight: bold;
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 5px;
  object-fit: cover;
}
</style>
