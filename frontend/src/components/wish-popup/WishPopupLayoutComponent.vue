<template>
    <div id="app-body">
        <div class="pro-bucket container" data-v-1eaba17a="">
            <div data-v-215721d8="" data-v-1eaba17a="">
                <div class="v-portal" style="display:none;" data-v-215721d8=""></div>
                <div class="v-portal" style="display:none;" data-v-215721d8=""></div>
                <div class="v-portal" style="display:none;" data-v-215721d8=""></div>
            </div>

            <header class="sub-header-desktop" data-v-1eaba17a="">
                <h1 class="heading-title" data-v-1eaba17a="">찜한 팝업</h1>
                <h3 class="sub-title" data-v-1eaba17a=""></h3>
            </header>

            <div class="content" data-v-1eaba17a="">
                <WishPopupAsideComponent></WishPopupAsideComponent>

                <main class="main" data-v-1eaba17a="">

                    <WishPopupHeaderComponent></WishPopupHeaderComponent>

                    <div class="divider" data-v-1eaba17a=""></div>


                    <WishPopupListComponent></WishPopupListComponent>



                    <div data-v-644ea9c9="" data-v-1eaba17a="" class="infinite-loading-container">
                        <div data-v-644ea9c9="" class="infinite-status-prompt" style="display: none;">
                            <div data-v-fc3fcce8="" data-v-1eaba17a="" class="" data-v-644ea9c9="">
                                <div data-v-fc3fcce8="" class="indicator-body center"
                                    style="width: 24px; height: 24px; border-width: 0.25rem; border-style: solid; border-color: rgb(0, 199, 174) rgba(0, 199, 174, 0.2) rgba(0, 199, 174, 0.2); border-image: initial;">
                                </div>
                                <p data-v-fc3fcce8="" style="display: none;"></p>
                            </div>
                        </div>
                        <div data-v-644ea9c9="" class="infinite-status-prompt"
                            style="color: rgb(102, 102, 102); font-size: 14px; padding: 10px 0px; display: none;"></div>
                        <div data-v-644ea9c9="" class="infinite-status-prompt"
                            style="color: rgb(102, 102, 102); font-size: 14px; padding: 10px 0px;"></div>
                        <div data-v-644ea9c9="" class="infinite-status-prompt"
                            style="color: rgb(102, 102, 102); font-size: 14px; padding: 10px 0px; display: none;">

                            <br data-v-644ea9c9=""> <button data-v-644ea9c9="" class="btn-try-infinite"></button>
                        </div>
                    </div>

                </main>
            </div>
        </div>
    </div>
</template>

<script>
import WishPopupAsideComponent from './WishPopupAsideComponent';
import WishPopupHeaderComponent from './WishPopupHeaderComponent';
import WishPopupListComponent from './WishPopupListComponent.vue';

export default {
    name: "WishPopupLayoutComponent",
    components: { WishPopupAsideComponent, WishPopupHeaderComponent, WishPopupListComponent, }
}
</script>

<style></style>