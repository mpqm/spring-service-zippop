<template>
  <article data-v-5e2a32d3="" data-v-17d8eba3="" class="product-list-item responsive">
    <a data-v-5e2a32d3="" href="/market/products/667130b7efd0c883a827f522?from=market_list" class=""
      data-testid="product-list-item">
      <div data-v-5e2a32d3="" class="item-image">
        <article data-v-dbfa36e6="" data-v-5e2a32d3="" class="preview-image">
          <div data-v-dbfa36e6="" class="image-wrap" style="padding-top: 66.6667%;">
            <img data-v-dbfa36e6="" data-src=""
              :src="popup.searchStoreImageResList[0].imageUrl" lazy="loaded">
          </div>
          <!----><!----><!---->
        </article>
      </div>
      <div data-v-5e2a32d3="" class="service-name">
        <div data-v-a3afae98="" data-v-5e2a32d3="" class="collapsed clamped">
          <div data-v-a3afae98="" class="line-clamp" style="line-height: 1.5; max-height: 3rem; -webkit-line-clamp: 2;">
            <h3 data-v-5e2a32d3="" data-v-a3afae98=""><strong>{{ popup.storeName }} </strong></h3>
          </div><!---->
        </div>
      </div>
      <div data-v-5e2a32d3="" class="item-name"><span data-v-5e2a32d3="">팝업 스토어 이름 : {{ popup.storeName }}</span>
      </div>
      <div data-v-5e2a32d3="" class="item-price"><strong data-v-5e2a32d3="">카테고리 : {{ popup.category }}</strong></div>
      <div data-v-5e2a32d3="" class="provider-review"><span data-v-0d243f1a="" data-v-5e2a32d3=""
          class="sg-icon-v1 star-icon" style="width: 0.875rem; height: 0.875rem;"><svg width="14" height="14"
            viewBox="0 0 14 14" xmlns="http://www.w3.org/2000/svg">
            <path
              d="m7.496 1.596 1.407 2.742 3.145.44c.91.127 1.275 1.204.615 1.822l-2.276 2.134.538 3.015c.155.872-.797 1.538-1.612 1.126L6.5 11.452l-2.813 1.423c-.815.412-1.767-.254-1.612-1.126l.538-3.015L.337 6.6c-.66-.618-.296-1.695.615-1.822l3.145-.44 1.407-2.742C5.912.8 7.088.8 7.496 1.596"
              fill="#FFCE21" fill-rule="evenodd"></path>
          </svg>
        </span><span data-v-5e2a32d3="" class="review-avg">{{ popup.likeCount }}</span><span data-v-5e2a32d3=""
          class="review-count"></span></div>
    </a>
  </article>
</template>

<script>
export default {
  name: "PopupItemComponent",
  data() {
    return {
      imageUrl: ""
    }
  },
  props: { popup: { type: Object, required: true } },
  methods: {
    setImageUrl() {
      this.imageUrl
    }
  }
}

</script>

<style scoped></style>