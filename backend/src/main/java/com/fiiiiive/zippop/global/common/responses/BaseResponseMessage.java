package com.fiiiiive.zippop.global.common.responses;

public enum BaseResponseMessage {
    // 200~500 Internal
    REQUEST_SUCCESS(true, 200, "요청이 정상적으로 처리되었습니다"),
    REQUEST_FAIL(false, 300, "요청을 실패했습니다."),
    DATABASE_SERVER_ERROR(false, 301, "DATABASE 오류"),
    INVALID_TOKEN(false, 302, "유효하지 않은 토큰입니다."),
    JWT_TOKEN_EXPIRED(false, 303, "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_UNSUPPORTED(false, 304, "JWT 토큰 형식이 아닙니다."),
    JWT_TOKEN_WRONG(false, 305, "JWT 토큰이 잘못됬습니다."),
    ACCESS_DENIED(false, 306, "접근이 거부되었습니다. 권한이 없습니다."),
    BAD_CREDENTIAL(false, 307, "아이디 또는 비밀번호가 맞지 않습니다. 다시 확인해 주세요"),
    USER_NOT_FOUND(false, 308, "계정이 존재하지 않습니다. 회원가입 진행 후 로그인 해주세요."),
    FILE_UPLOAD_FAIL(false, 309,"이미지 업로드에 실패했습니다."),
    AUTH_FAIL(false, 310, "사용자 인증에 실패하였습니다."),
    EMAIL_SEND_FAIL(false, 311, "이메일 전송에 실패했습니다."),
    UNPARSE_JSON(false, 312, "json 형식을 매핑할 수 없습니다."),
    INTERNAL_SERVER_ERROR(false, 500, "내부 서버 오류가 발생해서 처리할 수 없습니다."),
    IAMPORT_ERROR(false,  314,"PG 사에 오류가 발생했습니다. 관리자에게 문의해주세요."),

    // 회원 2000
    // 회원 가입
    AUTH_SIGNUP_SUCCESS(true, 2000, "이메일 인증을 완료해주세요 유효시간은 3분입니다."),
    AUTH_SIGNUP_SUCCESS_IS_INACTIVE(true, 2001, "비활성화된 계정입니다. 이메일 인증을 완료해 복구하세요, 유효시간은 3분입니다."),
    AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_CUSTOMER(false, 2002, "이미 고객 회원으로 가입된 계정입니다. 고객 회원은 기업 회원으로 회원가입 할 수 없습니다."),
    AUTH_SIGNUP_FAIL_ALREADY_REGISTER_AS_COMPANY(false, 2003, "이미 기업 회원으로 가입된 계정입니다. 기업 회원은 고객 회원으로 회원가입할 수 없습니다."),
    AUTH_SIGNUP_FAIL_ALREADY_EXIST(false, 2004, "이미 회원가입한 계정입니다."),
    // 이메일 인증
    AUTH_VERIFY_FAIL(false, 2004, "이메일 인증에 실패했습니다."),
    // 계정 비/활성화
    AUTH_INACTIVE_SUCCESS(true, 2005, "계정 비활성화에 성공했습니다."),
    AUTH_INACTIVE_FAIL(false, 2006, "계정 비활성화에 실패했습니다."),
    // 아이디 찾기
    AUTH_FIND_ID_SUCCESS(true, 2007, "이메일로 아이디 찾기 결과를 전송했습니다. 이메일을 확인해주세요"),
    AUTH_FIND_ID_FAIL_NOT_EMAIL_VERIFY(false, 2008, "이메일 인증이 되지않은 사용자는 아이디 찾기를 진행할 수 없습니다."),
    // 비밀번호 찾기
    AUTH_FIND_PASSWORD_SUCCESS(true, 2009, "이메일로 비밀번호 찾기 결과를 전송했습니다. 이메일을 확인해주세요"),
    AUTH_FIND_PASSWORD_FAIL_NOT_EMAIL_VERIFY(false, 2010, "이메일 인증이 되지않은 사용자는 비밀번호 찾기를 진행할 수 없습니다."),
    // 계정 정보 변경
    AUTH_EDIT_INFO_SUCCESS(true, 2011, "계정 프로필 정보 변경에 성공했습니다."),
    AUTH_EDIT_INFO_FAIL(false, 2012, "계정 프로필 정보 변경에 실패했습니다."),
    AUTH_EDIT_INFO_FAIL_NOT_FOUND_MEMBER(false, 2012, "사용자를 찾을 수 없습니다."),
    // 계정 패스워드 수정
    AUTH_EDIT_PASSWORD_SUCCESS(true, 2013, "계정 비밀번호 변경에 성공했습니다."),
    AUTH_EDIT_PASSWORD_FAIL(false, 2014, "계정 비밀번호 변경에 실패했습니다."),
    AUTH_EDIT_PASSWORD_FAIL_NOT_FOUND_MEMBER(false, 2015, "사용자를 찾을 수 없습니다."),
    AUTH_EDIT_PASSWORD_FAIL_PASSWORD_NOT_MATCH(false, 2016, "계정 비밃번호가 틀립니다."),
    // 프로필 정보 2070
    AUTH_GET_PROFILE_SUCCESS(true,2016,"프로필 조회에 성공했습니다"),
    AUTH_GET_PROFILE_FAIL(false,2017,"프로필 조회에 실패했습니다"),

    // 장바구니 3000
    // 장바구니 등록
    CART_REGISTER_SUCCESS(true, 3000  , "장바구니 등록에 성공했습니다." ),
    CART_REGISTER_FAIL_MEMBER_NOT_FOUND(false, 3001, "사용자를 찾을수 없습니다." ),
    CART_REGISTER_FAIL_GOODS_NOT_FOUND(false, 3002, "상품을 찾을 수 없습니다."),
    CART_REGISTER_FAIL_STORE_NOT_FOUND(false, 3003, "상품을 찾을 수 없습니다."),
    CART_REGISTER_FAIL_ITEM_EXIST(false, 3004, "해당 상품이 이미 장바구니에 존재 합니다."),
    // 장바구니 목록 조회
    CART_SEARCH_ALL_SUCCESS(true, 3005, "장바구니 목록을 불러왔습니다."),
    CART_SEARCH_ALL_FAIL_NOT_FOUND(false, 3006, "장바구니 목록을 찾을 수 없습니다."),
    // 장바구니 아이템 목록 조회
    CART_ITEM_SEARCH_ALL_SUCCESS(true, 3007, "장바구니 아이템 목록을 불러왔습니다."),
    CART_ITEM_SEARCH_ALL_FAIL_NOT_FOUND(false, 3008, "장바구니 아이템 목록을 찾을 수 없습니다."),
    // 장바구니 아이템 수량 조절
    CART_ITEM_COUNT_SUCCESS(true, 3009, "장바구니 아이템 수량 조절에 성공했습니다."),
    CART_ITEM_COUNT_FAIL_NOT_FOUND(false, 3010, "장바구니 아이템을 찾을 수 없습니다."),
    CART_ITEM_COUNT_FAIL_IS_ZERO(false, 3011, "장바구니 아이템의 수량이 0입니다."),
    // 장바구니 삭제
    CART_ITEM_DELETE_SUCCESS(true, 3012, "장바구니 아이템 삭제에 성공했습니다."),
    // 장바구니 전체삭제
    CART_ITEM_DELETE_ALL_SUCCESS(true, 3013, "장바구니 전체 삭제에 성공했습니다."),
    CART_DELETE_ALL_FAIL_NOT_FOUND(false, 3014, "장바구니를 찾을 수 없습니다."),

    // 팝업 스토어 4000
    // 팝업 스토어 등록
    STORE_REGISTER_SUCCESS(true, 4000, "팝업 스토어 등록에 성공했습니다."),
    STORE_REGISTER_FAIL_UNAUTHORIZED(false, 4001, "팝업 스토어 등록은 기업회원만 가능합니다."),
    // 팝업 스토어 단일 조회
    STORE_SEARCH_SUCCESS(true, 4002, "팝업 스토어 목록 조회에 성공했습니다."),
    STORE_SEARCH_FAIL_NOT_FOUND(false, 4003, "해당 팝업 스토어가 존재하지 않습니다."),
    // 팝업 스토어 목록 조회
    STORE_SEARCH_ALL_SUCCESS(true, 4004, "팝업 스토어 목록 조회에 성공했습니다."),
    STORE_SEARCH_ALL_FAIL_NOT_FOUND(false, 4005, "해당 팝업 스토어가 조재하지 않습니다."),
    // 팝업 스토어 수정
    STORE_UPDATE_SUCCESS(true, 4006, "팝업 스토어 수정에 성공했습니다."),
    STORE_UPDATE_FAIL_NOT_FOUND(false, 4007, "팝업 스토어를 찾을 수 없습니다."),
    STORE_UPDATE_FAIL_INVALID_MEMBER(false, 4008, "해당 팝업스토어를 등록한 기업회원이 아닙니다."),
    // 팝업 스토어 삭제
    STORE_DELETE_SUCCESS(true, 4009, "팝업 스토어 삭제에 성공했습니다."),
    STORE_DELETE_FAIL_NOT_FOUND(false, 4010, "팝업 스토어를 찾을 수 없어 삭제에 실패했습니다."),
    STORE_DELETE_FAIL_INVALID_MEMBER(false, 4011, "해당 팝업 스토어를 등록한 기업 회원이 아닙니다."),
    // 팝업 스토어 좋아요
    STORE_LIKE_SUCCESS(true, 4012, "팝업 스토어 좋아요 성공"),
    STORE_LIKE_FAIL_NOT_FOUND(false, 4013, "해당 팝업 스토어를 찾을 수 없습니다."),
    STORE_LIKE_FAIL_INVALID_MEMBER(false, 4014, "해당 팝업 스토어에서 결제한 내역이 없습니다."),
    STORE_LIKE_FAIL_INVALID_ROLE(false, 4015, "기업 회원은 좋아요 기능이 제한됩니다."),
    STORE_LIKE_SEARCH_ALL_SUCCESS(true, 4016, "팝업 스토어 좋아요 목록을 불러오는데 성공했습니다."),
    STORE_LIKE_SEARCH_ALL_FAIL_NOT_FOUND(false, 4017, "팝업 스토어 좋아요 목록을 찾을 수 없습니다."),
    // 팝업 스토어 리뷰
    STORE_REVIEW_SUCCESS(true, 4018, "팝업 스토어 리뷰 등록에 성공했습니다."),
    STORE_REVIEW_FAIL_INVALID_ROLE(false, 4019, "기업 회원은 리뷰 작성 기능이 제한 됩니다."),
    STORE_REVIEW_FAIL_NOT_FOUND(false, 4020, "해당 팝업 스토어를 찾을 수 없습니다."),
    STORE_REVIEW_FAIL_INVALID_MEMBER(false, 4021, "인증된 사용자만이 리뷰를 등록할 수 있습니다."),
    STORE_REVIEW_FAIL_DUPLICATED(false, 4022,"리뷰는 팝업스토어당 한 글씩만 적을 수 있습니다."),
    STORE_REVIEW_SEARCH_ALL_SUCCESS(true, 4023, "팝업 스토어 리뷰 목록을 불러왔습니다."),
    STORE_REVIEW_SEARCH_ALL_FAIL_NOT_FOUND(false, 4024, "팝업 스토어 리뷰 목록을 찾을 수 없습니다."),

    // 팝업 굿즈 5000
    // 팝업 굿즈 등록
    GOODS_REGISTER_SUCCESS(true, 5000,"팝업 굿즈 등록에 성공했습니다."),
    GOODS_REGISTER_FAIL_NOT_FOUND_STORE(false, 5001, "해당 스토어를 찾을 수 없습니다."),
    GOODS_REGISTER_FAIL_INVALID_MEMBER(false, 5002, "팝업 굿즈 등록은 팝업 스토어를 등록한 기업회원만 가능합니다."),
    // 팝업 굿즈 조회
    GOODS_SEARCH_SUCCESS(true, 5003, "팝업 굿즈 조회에 성공했습니다."),
    GOODS_SEARCH_FAIL_NOT_FOUND_STORE(false, 5004, "해당 스토어를 찾을 수 없습니다."),
    // 팝업 굿즈 목록 조회
    GOODS_SEARCH_ALL_SUCCESS(true, 5005, "팝업 굿즈 목록 조회에 성공했습니다."),
    GOODS_SEARCH_ALL_FAIL_STORE_NOT_NOT_FOUND(false, 5006, "해당 스토어를 찾을 수 없습니다."),
    // 팝업 굿즈 수정
    GOODS_UPDATE_SUCCESS(true, 5007, "팝업 굿즈 수정에 성공했습니다."),
    GOODS_UPDATE_FAIL_NOT_FOUND(false, 5008, "팝업 굿즈를 찾을 수 없습니다."),
    GOODS_UPDATE_FAIL_NOT_FOUND_INVALID_MEMBER(false, 5009, "팝업 굿즈 수정은 팝업 스토어를 등록한 기업회원만 가능합니다."),
    // 팝업 굿즈 삭제
    GOODS_DELETE_SUCCESS(true, 5010, "팝업 굿즈 삭제에 성공했습니다."),
    GOODS_DELETE_FAIL_NOT_FOUND(false, 5011, "팝업 스토어를 찾을 수 없어 삭제에 실패했습니다."),
    GOODS_DELETE_FAIL_INVALID_MEMBER(false, 5012, "해당 팝업 스토어를 등록한 기업 회원이 아닙니다."),

    // 굿즈 주문 6000
    // 굿즈 구매
    ORDERS_PAY_SUCCESS(true, 6000,"결제에 성공했습니다."),
    ORDERS_PAY_FAIL(false, 6001, "결제에 실패했습니다. 주문한 내역이 없습니다."),
    ORDERS_PAY_FAIL_INVALID_ROLE(false, 6002, "기업회원은 결제를 진행할 수 없습니다."),
    ORDERS_PAY_FAIL_NOT_FOUND_MEMBER(false, 6003, "결제 정보에 해당하는 유저가 없습니다."),
    ORDERS_PAY_FAIL_NOT_FOUND_GOODS(false, 6004, "결제 정보에 해당하는 팝업 굿즈가 없습니다."),
    ORDERS_PAY_FAIL_LIMIT_EXCEEDED(false, 6005, "사전 예매 굿즈는 품목 당 하나만 구매 가능합니다."),
    ORDERS_PAY_FAIL_POINT_EXCEEDED(false, 6006, "3000포인트 이상부터 사용할 수 있습니다."),
    ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE(false, 6007, "결제 금액이 잘못되었습니다."),
    // 굿즈 환불
    ORDERS_CANCEL_SUCCESS(true, 6008, "환불 요청에 성공했습니다."),
    ORDERS_CANCEL_FAIL_INVALID_ROLE(true, 6009, "기업회원은 요청할 수 없는 API 입니다."),
    ORDERS_CANCEL_FAIL_NOT_FOUND_MEMBER(false, 6010, "결제 정보에 해당하는 유저가 없습니다."),
    ORDERS_CANCEL_FAIL_NOT_FOUND(false, 6011, "해당 결재내역을 찾을 수 없습니다."),
    ORDERS_CANCEL_FAIL_ALREADY_CANCEL(false, 6012, "이미 환불 처리가 진행된 내역입니다."),
    ORDERS_CANCEL_FAIL_NOT_FOUND_GOODS(false, 6013, "결제 정보에 해당하는 팝업 굿즈가 없습니다."),
    ORDERS_CANCEL_FAIL_IS_DELIVERY(false, 6014, "배송 중인 건에 대해선 환불 을 지원하지 않습니다."),
    // 굿즈 구매 확정
    ORDERS_COMPLETE_SUCCESS(true, 6015, "배송 및 결제 확정 처리에 성공했습니다."),
    ORDERS_COMPLETE_FAIL_NOT_FOUND_STORE(false, 6016, "팝업 스토어를 찾을 수 없습니다."),
    ORDERS_COMPLETE_FAIL_INVALID_MEMBER(false, 6017, "해당 거래내역에 접근 권한이 없습니다."),
    ORDERS_COMPLETE_FAIL_IS_DELIVERY(false, 6018, "이미 주문 확정 처리 되고 배달 중입니다."),
    ORDERS_COMPLETE_FAIL_NOT_FOUND(false, 6019, "거래 내역을 찾을 수 없습니다."),
    ORDERS_COMPLETE_FAIL_IS_CANCEL(false, 6020, "결제 취소 건에 대해선 주문 확정을 지원하지 않습니다."),
    // 굿즈 구매 내역 조회
    ORDERS_SEARCH_SUCCESS(true, 6021, "결제 내역 조회에 성공했습니다."),
    ORDERS_SEARCH_FAIL_NOT_FOUND(false, 6022, "거래 내역을 찾을 수 없습니다."),
    ORDERS_SEARCH_FAIL_INVALID_MEMBER(false, 6023, "해당 거래내역에 접근 권한이 없습니다."),
    ORDERS_SEARCH_FAIL_NOT_FOUND_STORE(false, 6024, "팝업 스토어를 찾을 수 없습니다"),
    // 굿즈 구매 내역 목록 조회
    ORDERS_SEARCH_ALL_SUCCESS(true, 6025, "결제 내역 목록 조회에 성공했습니다."),
    ORDERS_SEARCH_ALL_FAIL_NOT_FOUND(false, 6026, "거래 내역을 찾을 수 없습니다."),
    ORDERS_SEARCH_ALL_FAIL_NOT_FOUND_STORE(false, 6027, "팝업 스토어를 찾을 수 없습니다"),
    ORDERS_SEARCH_ALL_FAIL_INVALID_MEMBER(false, 6028, "해당 거래내역에 접근 권한이 없습니다."),

    // ========================================================================================================================
    // 팝업 예약 9000
    // 팝업 예약 생성 9000
    POPUP_RESERVE_CREATE_SUCCESS(true, 9000, "예약 등록에 성공했습니다."),
    POPUP_RESERVE_CREATE_FAIL_TIME_CLOSED(false, 9001, "해당 시간대는 예약이 마감되었습니다."),
    POPUP_RESERVE_CREATE_FAIL_INVALID_MEMBER(false, 9002, "기업 회원이 아닌 회원은 예약을 생성할 수 없습니다."),
    POPUP_RESERVE_CREATE_FAIL_NOT_FOUND(false, 9003, "예약을 생성하려는 팝업스토어를 찾을 수 없습니다."),
    // 팝업 예약 취소 9100
    POPUP_RESERVE_CANCEL_SUCCESS(true, 9100, "예약 취소에 성공했습니다."),
    POPUP_RESERVE_CANCEL_FAIL(false,  9101, "예약 취소에 실패했습니다."),
    // 팝업 예약 접속 9200
    POPUP_RESERVE_ENROLL_SUCCESS(true, 9200, "예약에 성공했습니다."),
    POPUP_RESERVE_ENROLL_FAIL(false, 9201, "예약에 실패했습니다."),
    // 팝업 예약 조회 9300
    POPUP_RESERVE_SEARCH_STATUS_SUCCESS(true, 9300, "예약 대기자 및 Redis 상태를 불러왔습니다."),

    // ========================================================================================================================

    // 팝업 굿즈 주문 검증 4400


    // 팝업 굿즈 결제 4500


    // 결제
    // 기업 수수료 결제 4500



    // ========================================================================================================================
    // 찜 6000
    // 찜 등록 6100
    FAVORITE_ACTIVE_SUCCESS(true, 6100, "해당 팝업 스토어를 찜 목록에 추가했습니다."),
    FAVORITE_ACTIVE_FAIL_STORE_NOT_FOUND(false, 6101, "해당 팝업 스토어를 찾을 수 없습니다."),
    FAVORITE_ACTIVE_FAIL_MEMBER_NOT_FOUND(false, 6102, "유저를 찾을 수 없습니다."),
    FAVORITE_INACTIVE_SUCCESS(true, 6200, "해당 팝업 스토어를 찜 목록에서 뺏습니다."),
    // 찜 조회 6200
    FAVORITE_SEARCH_ALL_SUCCESS(true, 6200, "팝업 스토어 찜 목록을 불러오는데 성공했습니다."),
    FAVORITE_SEARCH_ALL_FAIL(false, 6201, "팝업 스토어 찜 목록을 불러오는데 실패했습니다."),



    // ========================================================================================================================
    // 채팅 8000
    // 채팅방 생성 8000
    CHAT_ROOM_CREATE_SUCCESS(true, 8000, "채팅방 생성에 성공했습니다."),
    CHAT_ROOM_CREATE_FAIL(false, 8001, "채팅방 생성에 실패했습니다."),
    // 채팅방 참여 8100
    CHAT_ROOM_JOIN_SUCCESS(true, 8100, "채팅방에 성공적으로 참여했습니다."),
    CHAT_ROOM_JOIN_FAIL(false, 8101, "채팅방 참여에 실패했습니다."),
    // 채팅방 나가기 8200
    CHAT_ROOM_LEAVE_SUCCESS(true, 8200, "채팅방에서 성공적으로 나갔습니다."),
    CHAT_ROOM_LEAVE_FAIL(false, 8201, "채팅방 나가기에 실패했습니다."),
    // 채팅방 사용자 조회 8300
    CHAT_USER_COUNT_SEARCH_SUCCESS(true, 8300, "채팅방 사용자 수 조회에 성공했습니다."),
    CHAT_USER_COUNT_SEARCH_FAIL(false, 8301, "채팅방 사용자 수 조회에 실패했습니다."),

    // 채팅방 기록 조회 8400
    CHAT_HISTORY_SEARCH_SUCCESS(true, 8500, "채팅 기록 조회에 성공했습니다."),
    CHAT_HISTORY_SEARCH_FAIL(false, 8501, "채팅 기록 조회에 실패했습니다."),
    // 채팅방 조회 8500
    CHAT_ROOM_SEARCH_SUCCESS(true, 8600, "채팅방 목록 조회에 성공했습니다."),
    CHAT_ROOM_SEARCH_FAIL(false, 8601, "채팅 기록 조회에 실패했습니다."),
    // 채팅 메세지 전송 8400
    CHAT_MESSAGE_SEND_SUCCESS(true, 8300, "채팅 메시지 전송에 성공했습니다."),
    CHAT_MESSAGE_SEND_FAIL(false, 8301, "채팅 메시지 전송에 실패했습니다."),

    //채팅 유저 확인 8500
    CHAT_USER_FOUND(true,8500,"사용자가 존재합니다."),
    CHAT_USER_NOT_FOUND(false,8401 ,"사용자가 존재하지 않습니다" );

    // ========================================================================================================================
    private Boolean success;
    private Integer code;
    private String message;

    BaseResponseMessage(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
    public static BaseResponseMessage findByCode(Integer code) {
        for (BaseResponseMessage message : values()) { if (message.getCode().equals(code)) { return message; }}
        return null;
    }
    public Boolean getSuccess() { return success; }

    public Integer getCode() { return code; }

    public String getMessage() { return message; }
}