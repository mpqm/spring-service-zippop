package com.fiiiiive.zippop.global.base;

import lombok.Getter;

@Getter
public enum SuccessCode {
    REQUEST_SUCCESS(200, "요청이 정상적으로 처리되었습니다"),
    AUTH_LOGIN_SUCCESS(1000, "로그인에 성공했습니다."),
    AUTH_LOGOUT_SUCCESS(1006, "로그아웃에 성공했습니다."),
    AUTH_SIGNUP_SUCCESS(2000, "이메일 인증을 완료해주세요 유효시간은 3분입니다."),
    AUTH_SIGNUP_SUCCESS_IS_INACTIVE(2001, "비활성화된 계정입니다. 이메일 인증을 완료해 복구하세요, 유효시간은 3분입니다."),
    AUTH_INACTIVE_SUCCESS(2006, "계정 비활성화에 성공했습니다."),
    AUTH_ACTIVE_SUCCESS(2008, "계정 복구 이메일을 전송했습니다. 이메일을 확인해주세요"),
    AUTH_FIND_ID_SUCCESS(2010, "이메일로 아이디 찾기 결과를 전송했습니다. 이메일을 확인해주세요"),
    AUTH_FIND_PW_SUCCESS(2013, "이메일로 비밀번호 찾기 결과를 전송했습니다. 이메일을 확인해주세요"),
    AUTH_EDIT_INFO_SUCCESS(2016, "계정 프로필 정보 변경에 성공했습니다."),
    AUTH_RESET_PW_SUCCESS(2019, "계정 비밀번호 변경에 성공했습니다."),
    AUTH_GET_PROFILE_SUCCESS(2021, "프로필 조회에 성공했습니다"),
    CART_REGISTER_SUCCESS(3000, "장바구니 등록에 성공했습니다."),
    CART_SEARCH_ALL_SUCCESS(3005, "장바구니 목록을 불러왔습니다."),
    CART_ITEM_SEARCH_ALL_SUCCESS(3007, "장바구니 아이템 목록을 불러왔습니다."),
    CART_ITEM_COUNT_SUCCESS(3009, "장바구니 아이템 수량 조절에 성공했습니다."),
    CART_ITEM_DELETE_SUCCESS(3012, "장바구니 아이템 삭제에 성공했습니다."),
    CART_ITEM_DELETE_ALL_SUCCESS(3013, "장바구니 전체 삭제에 성공했습니다."),
    STORE_REGISTER_SUCCESS(4000, "팝업 스토어 등록에 성공했습니다."),
    STORE_SEARCH_SUCCESS(4002, "팝업 스토어 목록 조회에 성공했습니다."),
    STORE_SEARCH_ALL_SUCCESS(4004, "팝업 스토어 목록 조회에 성공했습니다."),
    STORE_UPDATE_SUCCESS(4006, "팝업 스토어 수정에 성공했습니다."),
    STORE_DELETE_SUCCESS(4009, "팝업 스토어 삭제에 성공했습니다."),
    STORE_LIKE_SUCCESS(4012, "팝업 스토어 좋아요 성공"),
    STORE_LIKE_SEARCH_ALL_SUCCESS(4015, "팝업 스토어 좋아요 목록을 불러오는데 성공했습니다."),
    STORE_REVIEW_SUCCESS(4018, "팝업 스토어 리뷰 등록에 성공했습니다."),
    STORE_REVIEW_SEARCH_ALL_SUCCESS(4023, "팝업 스토어 리뷰 목록을 불러왔습니다."),
    GOODS_REGISTER_SUCCESS(5000, "팝업 굿즈 등록에 성공했습니다."),
    GOODS_SEARCH_SUCCESS(5003, "팝업 굿즈 조회에 성공했습니다."),
    GOODS_SEARCH_ALL_SUCCESS(5005, "팝업 굿즈 목록 조회에 성공했습니다."),
    GOODS_UPDATE_SUCCESS(5007, "팝업 굿즈 수정에 성공했습니다."),
    GOODS_DELETE_SUCCESS(5010, "팝업 굿즈 삭제에 성공했습니다."),
    ORDERS_PAY_SUCCESS(6000, "결제에 성공했습니다."),
    ORDERS_CANCEL_SUCCESS(6008, "환불 요청에 성공했습니다."),
    ORDERS_UPDATE_SUCCESS(6015, "결제 상태 변경에 성공했습니다."),
    ORDERS_COMPLETE_SUCCESS(6015, "배송 및 결제 확정 처리에 성공했습니다."),
    ORDERS_SEARCH_SUCCESS(6021, "결제 내역 조회에 성공했습니다."),
    ORDERS_SEARCH_ALL_SUCCESS(6025, "결제 내역 목록 조회에 성공했습니다."),
    RESERVE_REGISTER_SUCCESS(7000, "예약 등록에 성공했습니다."),
    RESERVE_ENROLL_SUCCESS(7005, "예약에 성공했습니다."),
    RESERVE_SEARCH_ALL_SUCCESS(7008, "예약 목록을 불러왔습니다."),
    RESERVE_CANCEL_SUCCESS(7008, "예약취소에 성공했습니다."),
    RESERVE_SEARCH_STATUS_SUCCESS(7010, "예약 대기자 및 Redis 상태를 불러왔습니다."),
    RESERVE_ACCESS_SUCCESS(7012, "유효 사용자입니다."),
    RESERVE_DELETE_SUCCESS(7014, "팝업스토어 예약 삭제에 서공했습니다."),
    PAYOUT_SEARCH_SUCCESS(8000, "팝업 스토어 정산 내역을 조회했습니다.");

    private final Integer code;
    private final String message;

    SuccessCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
