<template>
    <div id="app-body">
        <div data-v-1eaba17a="" class="pro-bucket container">
            <div data-v-215721d8="" data-v-1eaba17a="">
                <div data-v-215721d8="" class="v-portal" style="display: none;"></div>
                <div data-v-215721d8="" class="v-portal" style="display: none;"></div>
                <div data-v-215721d8="" class="v-portal" style="display: none;"></div>
            </div>
            <header data-v-1eaba17a="" class="sub-header-desktop">
                <h1 data-v-1eaba17a="" class="heading-title">찜한 팝업</h1>
                <h3 data-v-1eaba17a="" class="sub-title"></h3>
            </header>
            <div data-v-1eaba17a="" class="content">
                <aside data-v-1eaba17a="" class="aside">
                    <div data-v-b3ee3c58="" data-v-1eaba17a="" class="request-guide"><span data-v-b3ee3c58=""
                            class="desc">고수들에게 요청서를 한 번에 보내세요!</span><button data-v-b3ee3c58="" type="button"
                            class="btn request-btn btn-primary btn-block"> 팝업 찾아보기 </button></div>
                </aside>
                <main data-v-1eaba17a="" class="main">
                    <div data-v-1eaba17a="" class="top-mobile"><!----></div>
                    <div data-v-1eaba17a="" class="top-desktop">
                        <div data-v-47a56272="" data-v-1eaba17a="" class="actions">
                            <div data-v-47a56272="" class="all-check-wrapper"><input data-v-47a56272="" id="all-check"
                                    disabled="disabled" name="allCheck" type="checkbox" class="all-check"><label
                                    data-v-47a56272="" for="all-check"><span data-v-47a56272="" class="label-text">전체
                                        선택(0/0)</span></label></div>
                            <div data-v-47a56272="" class="delete">선택 항목 삭제</div><!---->
                        </div>
                    </div>
                    <div data-v-1eaba17a="" class="divider"></div>
                    <div data-v-50ee375a="" data-v-1eaba17a="" class="no-result"><img data-v-50ee375a=""
                            src="data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48c3ZnIHZlcnNpb249IjEuMSIgd2lkdGg9IjgwcHgiIGhlaWdodD0iODBweCIgdmlld0JveD0iMCAwIDgwLjAgODAuMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayI+PGRlZnM+PGNsaXBQYXRoIGlkPSJpMCI+PHBhdGggZD0iTTM3NSwwIEwzNzUsODEyIEwwLDgxMiBMMCwwIEwzNzUsMCBaIj48L3BhdGg+PC9jbGlwUGF0aD48Y2xpcFBhdGggaWQ9ImkxIj48cGF0aCBkPSJNNDAsMCBDNjIuMDkxNCwwIDgwLDE3LjkwODYgODAsNDAgQzgwLDYyLjA5MTQgNjIuMDkxNCw4MCA0MCw4MCBDMTcuOTA4Niw4MCAwLDYyLjA5MTQgMCw0MCBDMCwxNy45MDg2IDE3LjkwODYsMCA0MCwwIFoiPjwvcGF0aD48L2NsaXBQYXRoPjxsaW5lYXJHcmFkaWVudCBpZD0iaTIiIHgxPSIwcHgiIHkxPSI0MHB4IiB4Mj0iODBweCIgeTI9IjQwcHgiIGdyYWRpZW50VW5pdHM9InVzZXJTcGFjZU9uVXNlIj48c3RvcCBzdG9wLWNvbG9yPSIjMDBDN0FFIiBvZmZzZXQ9IjAlIj48L3N0b3A+PHN0b3Agc3RvcC1jb2xvcj0iIzRDQzhFNSIgb2Zmc2V0PSIxMDAlIj48L3N0b3A+PC9saW5lYXJHcmFkaWVudD48L2RlZnM+PGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoLTE0OC4wIC0zMjAuMCkiPjxyZWN0IHg9IjBweCIgeT0iMHB4IiB3aWR0aD0iMzc1cHgiIGhlaWdodD0iODEycHgiIGZpbGw9IiNGRkZGRkYiPjwvcmVjdD48ZyBjbGlwLXBhdGg9InVybCgjaTApIj48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgxNDguMCAzMjAuMCkiPjxnIGNsaXAtcGF0aD0idXJsKCNpMSkiPjxwb2x5Z29uIHBvaW50cz0iMCwwIDgwLDAgODAsODAgMCw4MCAwLDAiIHN0cm9rZT0ibm9uZSIgZmlsbD0idXJsKCNpMikiIG9wYWNpdHk9IjYwJSI+PC9wb2x5Z29uPjwvZz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgzMS41IDE5LjApIj48cGF0aCBkPSJNOC41LDE3IEMxMy4xOTQ0LDE3IDE3LDEzLjE5NDQgMTcsOC41IEMxNywzLjgwNTYgMTMuMTk0NCwwIDguNSwwIEMzLjgwNTYsMCAwLDMuODA1NiAwLDguNSBDMCwxMy4xOTQ0IDMuODA1NiwxNyA4LjUsMTcgWiIgc3Ryb2tlPSIjRkZGRkZGIiBzdHJva2Utd2lkdGg9IjMiIGZpbGw9Im5vbmUiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCI+PC9wYXRoPjwvZz48ZyB0cmFuc2Zvcm09InRyYW5zbGF0ZSgyMy41IDQzLjApIj48cGF0aCBkPSJNMzIsMTYgQzMyLDcuMTYzNCAyNC44MzY2LDAgMTYsMCBDNy4xNjM0LDAgMCw3LjE2MzQgMCwxNiIgc3Ryb2tlPSIjRkZGRkZGIiBzdHJva2Utd2lkdGg9IjMiIGZpbGw9Im5vbmUiIHN0cm9rZS1taXRlcmxpbWl0PSIxMCI+PC9wYXRoPjwvZz48L2c+PC9nPjwvZz48L3N2Zz4="
                            alt="{{ proBucketText }}가 없습니다." class="no-result-img">
                        <div data-v-50ee375a="" class="no-result-title">
                            <div data-v-50ee375a="" class="title-text">한 번의 예약으로</div>
                            <div data-v-50ee375a="" class="title-text">내가 선택한 팝업들을 만나봐요</div>
                        </div><span data-v-50ee375a="" class="no-result-text">찜한 팝업이 비어있어요</span>
                    </div>
                </main>
            </div><!---->
        </div>
    </div>
</template>

<script>
export default {
    name: "WishPopupComponent",
    data() {
        return {};
    }
}
</script>

<style scoped>
.grid[data-v-b3ee3c58] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-b3ee3c58] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-b3ee3c58] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-b3ee3c58] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-b3ee3c58] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-b3ee3c58] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-b3ee3c58] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-b3ee3c58] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-b3ee3c58] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-b3ee3c58] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-b3ee3c58] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-b3ee3c58] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-b3ee3c58] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-b3ee3c58] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-b3ee3c58] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-b3ee3c58] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-b3ee3c58] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-b3ee3c58] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-b3ee3c58] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-b3ee3c58] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-b3ee3c58] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-b3ee3c58] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-b3ee3c58] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-b3ee3c58] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-b3ee3c58] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-b3ee3c58] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-b3ee3c58] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-b3ee3c58] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-b3ee3c58] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-b3ee3c58] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-b3ee3c58] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-b3ee3c58] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-b3ee3c58] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-b3ee3c58] {
        grid-column: span 15
    }
}

.request-guide[data-v-b3ee3c58] {
    width: 100%;
    border: 0;
    position: fixed;
    bottom: 0;
    left: 0;
    padding: .5rem 1rem 1rem;
    background-color: #fff;
    z-index: 1
}

@media (min-width: 768px) {
    .request-guide[data-v-b3ee3c58] {
        border: .0625rem solid #f2f2f2;
        border-radius: 4px;
        padding: 1.25rem 1.25rem 1rem;
        position: static;
        z-index: unset
    }
}

.request-guide .desc[data-v-b3ee3c58] {
    font-size: 1rem;
    letter-spacing: -.01875rem;
    color: #323232;
    margin-bottom: 1rem;
    display: none
}

@media (min-width: 768px) {
    .request-guide .desc[data-v-b3ee3c58] {
        display: block
    }
}

.request-guide .request-btn[data-v-b3ee3c58] {
    font-size: 1rem;
    font-weight: 500;
    padding: .75rem 1rem
}

.grid[data-v-47a56272] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-47a56272] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-47a56272] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-47a56272] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-47a56272] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-47a56272] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-47a56272] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-47a56272] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-47a56272] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-47a56272] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-47a56272] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-47a56272] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-47a56272] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-47a56272] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-47a56272] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-47a56272] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-47a56272] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-47a56272] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-47a56272] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-47a56272] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-47a56272] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-47a56272] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-47a56272] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-47a56272] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-47a56272] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-47a56272] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-47a56272] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-47a56272] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-47a56272] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-47a56272] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-47a56272] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-47a56272] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-47a56272] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-47a56272] {
        grid-column: span 15
    }
}

.actions[data-v-47a56272] {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: .75rem 0 .875rem
}

@media (min-width: 768px) {
    .actions[data-v-47a56272] {
        padding-top: 0
    }
}

.actions .all-check-wrapper .all-check[data-v-47a56272] {
    display: none
}

.actions .all-check-wrapper .all-check~label[data-v-47a56272] {
    margin: 0;
    display: flex;
    align-items: center;
    cursor: pointer;
    line-height: 1.25rem;
    width: -moz-fit-content;
    width: fit-content
}

.actions .all-check-wrapper .all-check~label[data-v-47a56272]:before {
    content: "";
    display: block;
    position: absolute;
    width: 1.25rem;
    height: 1.25rem;
    background-image: url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMCIgaGVpZ2h0PSIyMCI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cmVjdCB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIGZpbGw9IiNFMUUxRTEiIHJ4PSIyIi8+PHBhdGggc3Ryb2tlPSIjRkZGIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMiIgZD0iTTUgOS4yMTlMOC4zIDEyLjVsNS44NjctNS44MzMiLz48L2c+PC9zdmc+)
}

.actions .all-check-wrapper .all-check:checked~label[data-v-47a56272]:before {
    background-image: url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMCIgaGVpZ2h0PSIyMCI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cmVjdCB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIGZpbGw9IiMwMEM3QUUiIHJ4PSIyIi8+PHBhdGggc3Ryb2tlPSIjRkZGIiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiIHN0cm9rZS13aWR0aD0iMiIgZD0iTTUgOS4yMTlMOC4zIDEyLjVsNS44NjctNS44MzMiLz48L2c+PC9zdmc+)
}

.actions .all-check-wrapper .label-text[data-v-47a56272] {
    line-height: 1.25rem;
    padding-left: 1.75rem;
    font-size: .875rem;
    color: #737373
}

.actions .delete[data-v-47a56272] {
    cursor: pointer;
    font-size: .875rem;
    color: #737373
}

.grid[data-v-50ee375a] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-50ee375a] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-50ee375a] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-50ee375a] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-50ee375a] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-50ee375a] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-50ee375a] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-50ee375a] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-50ee375a] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-50ee375a] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-50ee375a] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-50ee375a] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-50ee375a] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-50ee375a] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-50ee375a] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-50ee375a] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-50ee375a] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-50ee375a] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-50ee375a] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-50ee375a] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-50ee375a] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-50ee375a] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-50ee375a] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-50ee375a] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-50ee375a] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-50ee375a] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-50ee375a] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-50ee375a] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-50ee375a] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-50ee375a] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-50ee375a] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-50ee375a] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-50ee375a] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-50ee375a] {
        grid-column: span 15
    }
}

.no-result[data-v-50ee375a] {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    min-height: 50vh
}

.no-result .no-result-img[data-v-50ee375a] {
    width: 5rem
}

.no-result .no-result-title[data-v-50ee375a] {
    margin-top: .75rem;
    text-align: center
}

.no-result .no-result-title .title-text[data-v-50ee375a] {
    font-size: 1.125rem;
    color: #323232;
    font-weight: 500;
    line-height: 1.625rem
}

@media (min-width: 768px) {
    .no-result .no-result-title[data-v-50ee375a] {
        display: none
    }
}

.no-result .no-result-text[data-v-50ee375a] {
    font-size: .875rem;
    color: #b5b5b5;
    margin-top: .5rem
}

@media (min-width: 768px) {
    .no-result .no-result-text[data-v-50ee375a] {
        font-size: 1rem;
        color: #737373;
        margin-top: 1rem
    }
}

.grid[data-v-11ae1556] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-11ae1556] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-11ae1556] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-11ae1556] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-11ae1556] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-11ae1556] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-11ae1556] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-11ae1556] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-11ae1556] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-11ae1556] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-11ae1556] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-11ae1556] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-11ae1556] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-11ae1556] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-11ae1556] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-11ae1556] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-11ae1556] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-11ae1556] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-11ae1556] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-11ae1556] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-11ae1556] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-11ae1556] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-11ae1556] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-11ae1556] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-11ae1556] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-11ae1556] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-11ae1556] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-11ae1556] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-11ae1556] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-11ae1556] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-11ae1556] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-11ae1556] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-11ae1556] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-11ae1556] {
        grid-column: span 15
    }
}

.pro-bucket-card[data-v-11ae1556] {
    padding: .625rem .75rem;
    border: .0625rem solid #f2f2f2;
    border-radius: 8px;
    cursor: pointer;
    position: relative;
    overflow: hidden
}

@media (min-width: 768px) {
    .pro-bucket-card[data-v-11ae1556] {
        padding: 1rem
    }
}

.pro-bucket-card.selected[data-v-11ae1556] {
    border-color: #00c7ae;
    background-color: #fbfefe
}

.pro-bucket-card.selected .pro-name-wrapper .checked[data-v-11ae1556] {
    background: url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMCIgaGVpZ2h0PSIyMCI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNLTc0LTc1aDkxMHY1ODZILTc0eiIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKDEgMSkiPjxyZWN0IHdpZHRoPSIxOCIgaGVpZ2h0PSIxOCIgZmlsbD0iIzAwQzdBRSIgcng9IjkiLz48cGF0aCBzdHJva2U9IiNGRkYiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgc3Ryb2tlLXdpZHRoPSIyIiBkPSJNNSA4LjY4OEw3Ljg4IDExLjVsNS4xMi01Ii8+PC9nPjwvZz48L3N2Zz4=) no-repeat 100% 100%
}

.pro-bucket-card .pro-info[data-v-11ae1556] {
    flex: 1;
    display: inline-flex;
    flex-direction: column;
    padding-right: .25rem
}

@media (min-width: 768px) {
    .pro-bucket-card .pro-info[data-v-11ae1556] {
        padding-right: .5rem
    }
}

.pro-bucket-card .pro-name-wrapper[data-v-11ae1556] {
    display: flex
}

.pro-bucket-card .pro-name-wrapper .checked[data-v-11ae1556] {
    display: inline-block;
    position: relative;
    top: .1875rem;
    width: 1.25rem;
    height: 1.25rem;
    background: url(data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyMCIgaGVpZ2h0PSIyMCI+PGcgZmlsbD0ibm9uZSIgZmlsbC1ydWxlPSJldmVub2RkIj48cGF0aCBkPSJNLTc0LTc1aDkxMHY1ODZILTc0eiIvPjxnIHRyYW5zZm9ybT0idHJhbnNsYXRlKDEgMSkiPjxyZWN0IHdpZHRoPSIxOCIgaGVpZ2h0PSIxOCIgZmlsbD0iI0UxRTFFMSIgcng9IjkiLz48cGF0aCBzdHJva2U9IiNGRkYiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCIgc3Ryb2tlLXdpZHRoPSIyIiBkPSJNNSA4LjY4OEw3Ljg4IDExLjVsNS4xMi01Ii8+PC9nPjwvZz48L3N2Zz4=) no-repeat 100% 100%;
    margin-right: .5rem
}

.pro-bucket-card .pro-name-wrapper .pro-name[data-v-11ae1556] {
    font-size: 1rem;
    font-weight: 700;
    margin-bottom: .25rem;
    width: 90%
}

@media (min-width: 768px) {
    .pro-bucket-card .pro-name-wrapper .pro-name[data-v-11ae1556] {
        font-size: 1.125rem
    }
}

.pro-bucket-card .pro-introduction[data-v-11ae1556] {
    font-size: .75rem;
    color: #737373;
    margin-bottom: .375rem
}

.pro-bucket-card .pro-profile-picture[data-v-11ae1556] {
    width: 4rem;
    height: 4rem;
    border-radius: 12px
}

.pro-bucket-card .pro-extra-info[data-v-11ae1556] {
    font-size: .6875rem;
    color: #737373;
    margin-top: .625rem
}

@media (min-width: 768px) {
    .pro-bucket-card .pro-extra-info[data-v-11ae1556] {
        margin-top: .75rem
    }
}

.pro-bucket-card .pro-extra-info .avg-response-time[data-v-11ae1556]:before {
    content: "·";
    margin: 0 .25rem
}

.pro-bucket-card .pro-extra-info .avg-response-time.no-career[data-v-11ae1556]:before {
    content: "";
    margin: 0
}

.grid[data-v-a5bd2c08] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-a5bd2c08] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-a5bd2c08] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-a5bd2c08] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-a5bd2c08] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-a5bd2c08] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-a5bd2c08] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-a5bd2c08] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-a5bd2c08] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-a5bd2c08] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-a5bd2c08] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-a5bd2c08] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-a5bd2c08] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-a5bd2c08] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-a5bd2c08] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-a5bd2c08] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-a5bd2c08] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-a5bd2c08] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-a5bd2c08] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-a5bd2c08] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-a5bd2c08] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-a5bd2c08] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-a5bd2c08] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-a5bd2c08] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-a5bd2c08] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-a5bd2c08] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-a5bd2c08] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-a5bd2c08] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-a5bd2c08] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-a5bd2c08] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-a5bd2c08] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-a5bd2c08] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-a5bd2c08] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-a5bd2c08] {
        grid-column: span 15
    }
}

.pro-bucket-banner-mobile[data-v-a5bd2c08] {
    position: relative;
    height: 7.15rem;
    margin: 1.5rem 0 .475rem;
    display: block
}

@media (min-width: 768px) {
    .pro-bucket-banner-mobile[data-v-a5bd2c08] {
        display: none
    }
}

.pro-bucket-banner-mobile .title[data-v-a5bd2c08] {
    margin: 0 0 .5rem
}

.pro-bucket-banner-mobile .title[data-v-a5bd2c08] span {
    font-size: 1.25rem;
    font-weight: 700;
    line-height: 1.6;
    letter-spacing: 0;
    color: #323232;
    display: block
}

.pro-bucket-banner-mobile .desc[data-v-a5bd2c08] {
    font-size: .875rem;
    color: #737373
}

.pro-bucket-banner-mobile .banner-img[data-v-a5bd2c08] {
    position: absolute;
    z-index: -1;
    bottom: 0;
    right: 0;
    height: 5.625rem
}

.grid[data-v-215721d8] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-215721d8] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-215721d8] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-215721d8] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-215721d8] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-215721d8] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-215721d8] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-215721d8] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-215721d8] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-215721d8] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-215721d8] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-215721d8] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-215721d8] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-215721d8] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-215721d8] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-215721d8] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-215721d8] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-215721d8] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-215721d8] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-215721d8] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-215721d8] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-215721d8] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-215721d8] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-215721d8] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-215721d8] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-215721d8] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-215721d8] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-215721d8] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-215721d8] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-215721d8] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-215721d8] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-215721d8] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-215721d8] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-215721d8] {
        grid-column: span 15
    }
}

.mobile-header[data-v-215721d8] {
    display: flex;
    gap: .25rem
}

.mobile-header .header-title[data-v-215721d8] {
    font-size: 1.125rem;
    font-weight: 500;
    color: #323232;
    margin: 0;
    line-height: 100%
}

.grid[data-v-aa9331b2] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-aa9331b2] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-aa9331b2] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-aa9331b2] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-aa9331b2] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-aa9331b2] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-aa9331b2] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-aa9331b2] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-aa9331b2] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-aa9331b2] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-aa9331b2] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-aa9331b2] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-aa9331b2] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-aa9331b2] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-aa9331b2] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-aa9331b2] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-aa9331b2] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-aa9331b2] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-aa9331b2] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-aa9331b2] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-aa9331b2] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-aa9331b2] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-aa9331b2] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-aa9331b2] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-aa9331b2] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-aa9331b2] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-aa9331b2] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-aa9331b2] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-aa9331b2] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-aa9331b2] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-aa9331b2] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-aa9331b2] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-aa9331b2] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-aa9331b2] {
        grid-column: span 15
    }
}

.container[data-v-aa9331b2] {
    padding: 0;
    display: flex;
    flex-direction: column
}

.intro-wrapper[data-v-aa9331b2] {
    background-repeat: no-repeat;
    background-position: 50%;
    background-size: cover;
    text-align: left;
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 1.25rem;
    flex: 1 0 15rem
}

@media (min-width: 768px) {
    .intro-wrapper[data-v-aa9331b2] {
        text-align: center;
        border-radius: .25rem;
        justify-content: center;
        align-items: center;
        padding: 0;
        flex: 1 0 20rem
    }
}

.service-name[data-v-aa9331b2] {
    font-size: 1.5rem;
    font-weight: 500;
    line-height: 1.42;
    letter-spacing: -.03125rem;
    color: #fff;
    margin: 0
}

@media (min-width: 768px) {
    .service-name[data-v-aa9331b2] {
        font-size: 2.5rem;
        line-height: 1.45;
        letter-spacing: -.05rem
    }
}

.review-rate-wrapper[data-v-aa9331b2] {
    display: flex
}

.review-rate[data-v-aa9331b2] {
    align-self: center
}

.review-rate-desc[data-v-aa9331b2] {
    font-size: 1rem;
    font-weight: 500;
    line-height: 1.5;
    letter-spacing: -.01875rem;
    color: #ffce21;
    padding-left: .3125rem
}

.service-detail-wrapper[data-v-aa9331b2] {
    margin-top: 1.25rem
}

.service-detail[data-v-aa9331b2] {
    display: inline-block;
    padding-right: 1.25rem
}

@media (min-width: 768px) {
    .service-detail[data-v-aa9331b2] {
        padding-right: 1.875rem
    }
}

.service-detail+.service-detail[data-v-aa9331b2] {
    padding-left: 1.25rem;
    border-left: .0625rem solid #fff;
    border-left: .0625rem solid hsla(0, 0%, 100%, .4)
}

@media (min-width: 768px) {
    .service-detail+.service-detail[data-v-aa9331b2] {
        padding-left: 1.875rem
    }
}

.service-detail>h5[data-v-aa9331b2] {
    font-size: 1.25rem;
    font-weight: 500;
    line-height: 1.4;
    letter-spacing: -.025rem;
    color: #fff;
    margin: 0
}

@media (min-width: 768px) {
    .service-detail>h5[data-v-aa9331b2] {
        font-size: 1.5rem;
        line-height: 1.42;
        letter-spacing: -.03125rem
    }
}

.service-detail>p[data-v-aa9331b2] {
    font-size: .75rem;
    line-height: 1.5;
    letter-spacing: -.00625rem;
    color: #fff;
    margin: 0
}

@media (min-width: 768px) {
    .service-detail>p[data-v-aa9331b2] {
        font-size: .875rem;
        line-height: 1.43;
        letter-spacing: -.0125rem
    }
}

.intro-content[data-v-aa9331b2] {
    text-align: left;
    height: 100%;
    padding: 1.25rem;
    display: flex;
    flex-direction: column
}

@media (min-width: 768px) {
    .intro-content[data-v-aa9331b2] {
        text-align: center;
        padding: 2.5rem
    }
}

.intro-content .intro-content-text[data-v-aa9331b2] {
    font-size: 1.125rem;
    font-weight: 500;
    color: #323232;
    margin-bottom: 1.25rem;
    line-height: normal
}

@media (min-width: 768px) {
    .intro-content .intro-content-text[data-v-aa9331b2] {
        font-size: 1.5rem
    }
}

.intro-content>p[data-v-aa9331b2] {
    font-size: .875rem;
    color: #737373;
    margin: 0
}

.request-start-btn-wrapper[data-v-aa9331b2] {
    display: flex;
    flex-direction: column;
    border: none;
    align-items: center;
    padding: 0 0 2.5rem
}

@media (max-width: 991.98px) {
    .request-start-btn-wrapper[data-v-aa9331b2] {
        padding: .875rem 1rem;
        border-top: .0625rem solid #f2f2f2;
        position: fixed;
        bottom: 0;
        margin: 0;
        width: 100%;
        z-index: 1;
        left: 50%;
        transform: translateX(-50%)
    }
}

.request-start-btn-wrapper .request-start-btn[data-v-aa9331b2] {
    background-color: #00c7ae;
    font-size: 1rem;
    font-weight: 500;
    color: #fff;
    border-radius: .25rem;
    width: 100%;
    border: none;
    padding: .75rem 3.75rem
}

.request-start-btn-wrapper .request-start-btn[data-v-aa9331b2]:focus {
    outline: none
}

@media (min-width: 768px) {
    .request-start-btn-wrapper .request-start-btn[data-v-aa9331b2] {
        width: 11.25rem
    }
}

.grid {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>* {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1 {
    grid-column: span 1
}

.grid>.span-md-2 {
    grid-column: span 2
}

.grid>.span-md-3 {
    grid-column: span 3
}

.grid>.span-md-4 {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid {
        padding: 0 7.5rem
    }

    .grid.max-col-8 {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>* {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1 {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2 {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3 {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4 {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5 {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6 {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7 {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8 {
        grid-column: span 8
    }

    .grid.max-col-15 {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>* {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1 {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2 {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3 {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4 {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5 {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6 {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7 {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8 {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9 {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10 {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11 {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12 {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13 {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14 {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15 {
        grid-column: span 15
    }
}

.request-form-modal {
    -webkit-overflow-scrolling: touch
}

.request-form-modal .modal-dialog {
    max-width: 35rem;
    height: 100%;
    padding: 0;
    margin: 0 auto !important
}

.request-form-modal .modal-content {
    height: 100%
}

@media (min-width: 768px) {
    .request-form-modal .modal-content {
        height: calc(100vh - 10rem);
        margin: auto 0;
        border-radius: .25rem
    }
}

.request-form-modal .modal-body {
    display: flex;
    height: 100%;
    padding: 0;
    margin: 0
}

.request-form-modal .modal-header {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 2;
    width: 100%;
    height: 3.625rem;
    padding: 1rem;
    border: none
}

@media (min-width: 768px) {
    .request-form-modal .modal-header {
        height: auto;
        padding: 2.5rem 2.5rem 0;
        border: none
    }
}

.grid[data-v-1eaba17a] {
    display: grid;
    -moz-column-gap: 1rem;
    column-gap: 1rem;
    padding: 0 1rem;
    max-width: 90rem;
    margin: 0 auto;
    grid-template-columns: repeat(4, 1fr)
}

.grid>[data-v-1eaba17a] {
    grid-column: span 4;
    width: 100% !important;
    max-width: 100% !important
}

.grid>.span-md-1[data-v-1eaba17a] {
    grid-column: span 1
}

.grid>.span-md-2[data-v-1eaba17a] {
    grid-column: span 2
}

.grid>.span-md-3[data-v-1eaba17a] {
    grid-column: span 3
}

.grid>.span-md-4[data-v-1eaba17a] {
    grid-column: span 4
}

@media (min-width: 992px) {
    .grid[data-v-1eaba17a] {
        padding: 0 7.5rem
    }

    .grid.max-col-8[data-v-1eaba17a] {
        grid-template-columns: repeat(8, 1fr)
    }

    .grid.max-col-8>[data-v-1eaba17a] {
        grid-column: span 8
    }

    .grid.max-col-8>.span-lg-1[data-v-1eaba17a] {
        grid-column: span 1
    }

    .grid.max-col-8>.span-lg-2[data-v-1eaba17a] {
        grid-column: span 2
    }

    .grid.max-col-8>.span-lg-3[data-v-1eaba17a] {
        grid-column: span 3
    }

    .grid.max-col-8>.span-lg-4[data-v-1eaba17a] {
        grid-column: span 4
    }

    .grid.max-col-8>.span-lg-5[data-v-1eaba17a] {
        grid-column: span 5
    }

    .grid.max-col-8>.span-lg-6[data-v-1eaba17a] {
        grid-column: span 6
    }

    .grid.max-col-8>.span-lg-7[data-v-1eaba17a] {
        grid-column: span 7
    }

    .grid.max-col-8>.span-lg-8[data-v-1eaba17a] {
        grid-column: span 8
    }

    .grid.max-col-15[data-v-1eaba17a] {
        grid-template-columns: repeat(15, 1fr)
    }

    .grid.max-col-15>[data-v-1eaba17a] {
        grid-column: span 15
    }

    .grid.max-col-15>.span-lg-1[data-v-1eaba17a] {
        grid-column: span 1
    }

    .grid.max-col-15>.span-lg-2[data-v-1eaba17a] {
        grid-column: span 2
    }

    .grid.max-col-15>.span-lg-3[data-v-1eaba17a] {
        grid-column: span 3
    }

    .grid.max-col-15>.span-lg-4[data-v-1eaba17a] {
        grid-column: span 4
    }

    .grid.max-col-15>.span-lg-5[data-v-1eaba17a] {
        grid-column: span 5
    }

    .grid.max-col-15>.span-lg-6[data-v-1eaba17a] {
        grid-column: span 6
    }

    .grid.max-col-15>.span-lg-7[data-v-1eaba17a] {
        grid-column: span 7
    }

    .grid.max-col-15>.span-lg-8[data-v-1eaba17a] {
        grid-column: span 8
    }

    .grid.max-col-15>.span-lg-9[data-v-1eaba17a] {
        grid-column: span 9
    }

    .grid.max-col-15>.span-lg-10[data-v-1eaba17a] {
        grid-column: span 10
    }

    .grid.max-col-15>.span-lg-11[data-v-1eaba17a] {
        grid-column: span 11
    }

    .grid.max-col-15>.span-lg-12[data-v-1eaba17a] {
        grid-column: span 12
    }

    .grid.max-col-15>.span-lg-13[data-v-1eaba17a] {
        grid-column: span 13
    }

    .grid.max-col-15>.span-lg-14[data-v-1eaba17a] {
        grid-column: span 14
    }

    .grid.max-col-15>.span-lg-15[data-v-1eaba17a] {
        grid-column: span 15
    }
}

.pro-bucket[data-v-1eaba17a] {
    min-height: 50vh;
    max-width: 60.625rem;
    padding: 0 0 3rem
}

.pro-bucket .sub-header-desktop[data-v-1eaba17a] {
    margin: 3rem 0 3.5rem;
    display: none
}

@media (min-width: 768px) {
    .pro-bucket .sub-header-desktop[data-v-1eaba17a] {
        display: block
    }
}

.pro-bucket .sub-header-desktop .heading-title[data-v-1eaba17a] {
    font-size: 2.125rem;
    font-weight: 500;
    line-height: normal;
    color: #323232;
    margin: 0 0 2.5rem
}

.pro-bucket .sub-header-desktop .sub-title[data-v-1eaba17a] {
    font-size: .875rem;
    font-weight: 500;
    margin: 0;
    line-height: normal;
    color: #525252
}

.pro-bucket .sub-header-mobile[data-v-1eaba17a] {
    margin: 1rem 0 .875rem
}

@media (min-width: 768px) {
    .pro-bucket .sub-header-mobile[data-v-1eaba17a] {
        display: none
    }
}

.pro-bucket .sub-header-mobile.for-fixed[data-v-1eaba17a] {
    margin-top: 4.5625rem
}

.pro-bucket .sub-header-mobile .sub-title[data-v-1eaba17a] {
    font-size: .875rem;
    font-weight: 700;
    color: #737373;
    margin: 0;
    line-height: normal
}

.pro-bucket .content[data-v-1eaba17a] {
    display: flex;
    gap: 0
}

@media (min-width: 768px) {
    .pro-bucket .content[data-v-1eaba17a] {
        gap: 3.625rem
    }
}

.pro-bucket .content .aside[data-v-1eaba17a] {
    width: 17.625rem;
    flex: 0
}

@media (min-width: 768px) {
    .pro-bucket .content .aside[data-v-1eaba17a] {
        flex: unset
    }
}

.pro-bucket .content .main[data-v-1eaba17a] {
    flex: 1;
    width: 100%
}

@media (min-width: 768px) {
    .pro-bucket .content .main[data-v-1eaba17a] {
        max-width: 39.375rem
    }
}

@media (min-width: 768px) {
    .pro-bucket .content .main .top-mobile[data-v-1eaba17a] {
        display: none
    }
}

.pro-bucket .content .main .top-mobile .pro-bucket-actions[data-v-1eaba17a] {
    background-color: #fff
}

.pro-bucket .content .main .top-mobile .pro-bucket-actions.fixed[data-v-1eaba17a] {
    position: fixed;
    top: 3.625rem;
    left: 0;
    width: 100%;
    padding-left: 1rem;
    padding-right: 1rem;
    border-bottom: .0625rem solid #f2f2f2;
    z-index: 1
}

.pro-bucket .content .main .top-desktop[data-v-1eaba17a] {
    display: none
}

@media (min-width: 768px) {
    .pro-bucket .content .main .top-desktop[data-v-1eaba17a] {
        display: block
    }
}

.pro-bucket .content .main .divider[data-v-1eaba17a] {
    width: 100%;
    height: .0625rem;
    background-color: #fafafa;
    margin: 1.0625rem 0 1.5rem;
    display: none
}

@media (min-width: 768px) {
    .pro-bucket .content .main .divider[data-v-1eaba17a] {
        display: block
    }
}

.pro-bucket .content .main .pro-bucket-list-item[data-v-1eaba17a] {
    margin-bottom: .75rem
}

@media (min-width: 768px) {
    .pro-bucket .content .main .pro-bucket-list-item[data-v-1eaba17a] {
        margin-bottom: 1rem
    }
}
</style>