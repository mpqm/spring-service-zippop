# ZIPPOP 백엔드 API 명세서

> 상태: CURRENT
> 기준 일자: 2026-08-23
> Base URL: 확인 필요
> Source: input/docs/spring-service-zippop/v2/zippopv2-백엔드 API 명세서

> Spring Boot 현재 구현 기준입니다.  
> 공통 응답 / 인증 / 도메인별 API를 정리합니다.

---

## 2 공통 규약

### 응답 형식 SuccessResponse<T>

<table>
  <thead>
    <tr>
      <th>Field</th>
      <th>Type</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>success</td>
      <td>Boolean</td>
      <td>성공 여부</td>
    </tr>
    <tr>
      <td>code</td>
      <td>Integer</td>
      <td>비즈니스 코드</td>
    </tr>
    <tr>
      <td>message</td>
      <td>String</td>
      <td>메시지</td>
    </tr>
    <tr>
      <td>result</td>
      <td>T | null</td>
      <td>페이로드</td>
    </tr>
  </tbody>
</table>

### 인증

<table>
  <thead>
    <tr>
      <th>항목</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Access Token</td>
      <td>쿠키 ATOKEN (1시간)</td>
    </tr>
    <tr>
      <td>Refresh Token</td>
      <td>쿠키 RTOKEN (5일), Redis refreshToken:{userId}</td>
    </tr>
    <tr>
      <td>예약 토큰</td>
      <td>쿠키 WTOKEN (대기열 승격 시, 10분)</td>
    </tr>
    <tr>
      <td>Cookie 속성</td>
      <td>HttpOnly, Secure, Path=/, SameSite=None</td>
    </tr>
    <tr>
      <td>역할</td>
      <td>ROLE_CUSTOMER, ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

### 공통 에러

<table>
  <thead>
    <tr>
      <th>상황</th>
      <th>HTTP</th>
      <th>code</th>
      <th>message</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Validation 실패</td>
      <td>400</td>
      <td>315</td>
      <td>입력값이 잘못되었습니다.</td>
    </tr>
    <tr>
      <td>BaseException</td>
      <td>400</td>
      <td>(해당 코드)</td>
      <td>BaseMessage</td>
    </tr>
    <tr>
      <td>AccessDenied</td>
      <td>403</td>
      <td>403</td>
      <td>접근이 거부되었습니다.</td>
    </tr>
    <tr>
      <td>Bad Credential</td>
      <td>401</td>
      <td>307</td>
      <td>아이디 또는 비밀번호가 틀렸습니다.</td>
    </tr>
    <tr>
      <td>JWT 만료</td>
      <td>401</td>
      <td>303</td>
      <td>JWT 토큰이 만료되었습니다.</td>
    </tr>
  </tbody>
</table>

### Page 응답 (result)

content, totalElements, totalPages, size, number, first, last, empty 등 Spring Data Page 직렬화

### Enum

<table>
  <thead>
    <tr>
      <th>이름</th>
      <th>값</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>PopupStatus</td>
      <td>POPUP_START, POPUP_END, POPUP_RESERVE, POPUP_STOCK</td>
    </tr>
    <tr>
      <td>OrdersStatus</td>
      <td>RESERVE_READY, RESERVE_DELIVERY, RESERVE_CANCEL, RESERVE_COMPLETE, STOCK_READY, STOCK_DELIVERY, STOCK_CANCEL, STOCK_COMPLETE</td>
    </tr>
    <tr>
      <td>GoodsStatus</td>
      <td>GOODS_RESERVED, GOODS_STOCK 등</td>
    </tr>
    <tr>
      <td>Operation</td>
      <td>increment, decrement</td>
    </tr>
    <tr>
      <td>Role</td>
      <td>ROLE_CUSTOMER, ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

---

## 3 회원 / 인증

<details>
<summary><strong>[POST] 회원가입</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>고객/기업 회원가입 (role 분기 단일 API). 인증 메일 발송</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Part</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>req</td>
      <td>CreateAccountReq (JSON)</td>
    </tr>
    <tr>
      <td>file</td>
      <td>MultipartFile (프로필, 선택)</td>
    </tr>
  </tbody>
</table>

**req body**

<pre><code data-language="json">{
  "role": "ROLE_CUSTOMER",
  "userId": "test01",
  "email": "test@example.com",
  "password": "pass1234",
  "name": "홍길동",
  "phoneNumber": "01012345678",
  "address": "서울시 강남구 ...,상세주소",
  "crn": null
}</code></pre>

<table>
  <thead>
    <tr>
      <th>필드</th>
      <th>제약</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>role</td>
      <td>필수. ROLE_CUSTOMER / ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>userId</td>
      <td>필수, 5~20자, 중복 불가</td>
    </tr>
    <tr>
      <td>email</td>
      <td>필수, 이메일 형식, 중복 불가</td>
    </tr>
    <tr>
      <td>password</td>
      <td>필수, min 4, BCrypt 저장</td>
    </tr>
    <tr>
      <td>name</td>
      <td>필수, max 50</td>
    </tr>
    <tr>
      <td>phoneNumber</td>
      <td>필수, ^01[0-9]\d{3,4}\d{4}$</td>
    </tr>
    <tr>
      <td>address</td>
      <td>필수, max 200</td>
    </tr>
    <tr>
      <td>crn</td>
      <td>기업만</td>
    </tr>
  </tbody>
</table>

- ✅ **Response 200 / 성공 코드 2000 or 2001**

<pre><code data-language="json">{
  "success": true,
  "code": 2000,
  "message": "회원가입에 성공했습니다. 이메일을 확인해주세요.(유효시간 3분)",
  "result": null
}</code></pre>

- ❌ **Error**

<table>
  <thead>
    <tr>
      <th>code</th>
      <th>message</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>2004</td>
      <td>이미 가입된 회원입니다.</td>
    </tr>
    <tr>
      <td>2023</td>
      <td>이미 존재하는 아이디입니다.</td>
    </tr>
    <tr>
      <td>2025</td>
      <td>올바르지 않은 역할입니다.</td>
    </tr>
  </tbody>
</table>

---

</details>

<details>
<summary><strong>[GET] 이메일 인증</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>Redis UUID 검증 후 계정 활성화, 프론트로 302 리다이렉트</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/verification</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>Type</th>
      <th>설명</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>email</td>
      <td>String</td>
      <td>이메일</td>
    </tr>
    <tr>
      <td>role</td>
      <td>String</td>
      <td>ROLE_CUSTOMER / ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>uuid</td>
      <td>String</td>
      <td>인증 UUID</td>
    </tr>
  </tbody>
</table>

- ✅ **Response 302** → http://localhost:8081/login?success=true
- ❌ **실패 302** → http://localhost:8081/login?error=true (code 2005)

---

</details>

<details>
<summary><strong>[POST] 로그인</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>userId/password 인증 후 JWT 쿠키 발급</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/auth/login</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>application/json</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "userId": "test01",
  "password": "pass1234"
}</code></pre>

- ✅ **Response 200 / code 1000**

<pre><code data-language="json">{
  "success": true,
  "code": 1000,
  "message": "로그인에 성공했습니다.",
  "result": null
}</code></pre>

Set-Cookie: ATOKEN, RTOKEN

- ❌ **Error**

<table>
  <thead>
    <tr>
      <th>code</th>
      <th>message</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1001</td>
      <td>아이디를 입력해주세요.</td>
    </tr>
    <tr>
      <td>1002</td>
      <td>비밀번호를 입력해주세요.</td>
    </tr>
    <tr>
      <td>307</td>
      <td>아이디 또는 비밀번호가 틀렸습니다.</td>
    </tr>
    <tr>
      <td>316</td>
      <td>비활성화된 회원입니다.</td>
    </tr>
  </tbody>
</table>

---

</details>

<details>
<summary><strong>[POST] 로그아웃</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>인증 쿠키 삭제</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/auth/logout</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No (쿠키 삭제)</td>
    </tr>
  </tbody>
</table>

- ✅ **Response 200**

<pre><code data-language="json">{
  "success": true,
  "code": 2080,
  "message": "로그아웃에 성공했습니다.",
  "result": null
}</code></pre>

---

</details>

<details>
<summary><strong>[GET] 소셜 로그인 (OAuth2)</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>카카오 OAuth2 로그인 (네이버/구글은 설정만 존재)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/oauth2/authorization/kakao</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
    <tr>
      <td>Callback</td>
      <td>/login/oauth2/code/kakao</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>처리</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>신규</td>
      <td>ROLE_CUSTOMER 생성, point 3000, isEmailAuth=true</td>
    </tr>
    <tr>
      <td>성공</td>
      <td>ATOKEN 쿠키 발급 후 리다이렉트</td>
    </tr>
    <tr>
      <td>상태</td>
      <td>카카오만 구현 (부분)</td>
    </tr>
  </tbody>
</table>

---

</details>

<details>
<summary><strong>[POST] 아이디 찾기</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>이메일로 계정 ID 발송</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/id/find</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "role": "ROLE_CUSTOMER",
  "email": "test@example.com"
}</code></pre>

- ✅ **code 2010** — 아이디 찾기 성공
- ❌ **2011** 이메일 미인증 / **2012** 회원 없음

---

</details>

<details>
<summary><strong>[POST] 비밀번호 찾기</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>임시 비밀번호 생성·저장 후 이메일 발송</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/password/find</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "role": "ROLE_CUSTOMER",
  "userId": "test01"
}</code></pre>

- ✅ **code 2013**
- ❌ **2014** 발급 불가 / **2015** 회원 없음

---

</details>

<details>
<summary><strong>[GET] 회원 정보 조회</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>로그인 회원 프로필 조회</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/me</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>Yes</td>
    </tr>
  </tbody>
</table>

- ✅ **code 2021**

<pre><code data-language="json">{
  "success": true,
  "code": 2021,
  "message": "회원 정보 조회에 성공했습니다.",
  "result": {
    "name": "홍길동",
    "email": "test@example.com",
    "role": "ROLE_CUSTOMER",
    "point": 3000,
    "phoneNumber": "01012345678",
    "address": "서울시 ...,상세",
    "crn": null,
    "profileImageUrl": "https://..."
  }
}</code></pre>

---

</details>

<details>
<summary><strong>[PATCH] 회원 정보 수정</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>프로필 수정</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/me</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>Yes</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Part</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>req</td>
      <td>UpdateAccountReq</td>
    </tr>
    <tr>
      <td>file</td>
      <td>프로필 이미지 (선택)</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "name": "홍길동",
  "phoneNumber": "01012345678",
  "address": "서울시 ...,상세",
  "crn": null,
  "profileImageUrl": "https://..."
}</code></pre>

- ✅ **code 2016**
- ❌ **2018** 회원 없음

---

</details>

<details>
<summary><strong>[PATCH] 비밀번호 변경</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>기존 비밀번호 검증 후 변경</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/password/reset</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>Yes</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "originPassword": "oldPass",
  "newPassword": "newPass"
}</code></pre>

- ✅ **code 2019**
- ❌ **2022** 비밀번호 불일치

---

</details>

<details>
<summary><strong>[DELETE] 계정 비활성화</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>소프트 비활성 처리</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/me</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>Yes</td>
    </tr>
  </tbody>
</table>

- ✅ **code 2006**

---

</details>

<details>
<summary><strong>[POST] 계정 활성화 요청</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>비활성 계정 인증 메일 재발송</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/accounts/me/activation</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "role": "ROLE_CUSTOMER",
  "email": "test@example.com"
}</code></pre>

- ✅ **code 2008**
- ❌ **2009** 실패 / **2010** 이미 활성

---

</details>

## 4 장바구니

> 전체 /api/v1/carts/** → **ROLE_CUSTOMER**

<details>
<summary><strong>[POST] 카트 등록</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>팝업 단위 장바구니에 굿즈 담기</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/carts</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "goodsIdx": 1,
  "popupIdx": 1
}</code></pre>

- ✅ **HTTP 201 / code 3000**
- ❌ **3002** 상품 없음 / **3004** 이미 존재

---

</details>

<details>
<summary><strong>[GET] 카트 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>내 장바구니(팝업 단위) 페이징</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/carts</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>Default</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>page</td>
      <td>0</td>
    </tr>
    <tr>
      <td>size</td>
      <td>10</td>
    </tr>
  </tbody>
</table>

- ✅ **code 3005** + Page<GetCartRes>

GetCartRes: popupIdx, companyEmail, popupName, popupContent, popupAddress, category, likeCount, totalPeople, popupStatus, popupStartDate, popupEndDate, createdAt, updatedAt, getPopupImageResList

---

</details>

<details>
<summary><strong>[GET] 카트 아이템 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>특정 카트의 아이템 목록</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/carts/{cartIdx}/items</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 3007**

<pre><code data-language="json">{
  "success": true,
  "code": 3007,
  "message": "장바구니 아이템 목록 조회에 성공했습니다.",
  "result": [
    {
      "cartItemIdx": 1,
      "count": 1,
      "price": 15000,
      "getGoodsRes": { "goodsIdx": 1, "goodsName": "...", "goodsPrice": 15000 }
    }
  ]
}</code></pre>

---

</details>

<details>
<summary><strong>[PATCH] 수량 조절</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>아이템 수량 증가/감소</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/carts/{cartIdx}/items/{cartItemIdx}/quantity</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>값</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>operation</td>
      <td>increment | decrement</td>
    </tr>
  </tbody>
</table>

- ✅ **code 3009**
- ❌ **3010** 없음 / **3011** 수량 1 이하

---

</details>

<details>
<summary><strong>[DELETE] 아이템 삭제</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/carts/{cartIdx}/items/{cartItemIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 3012**

---

</details>

<details>
<summary><strong>[DELETE] 카트 전체 삭제</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/carts/{cartIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 3013**
- ❌ **3014** 없음 / **3015** 권한 없음

---

</details>

## 5 팝업 스토어

<details>
<summary><strong>[POST] 팝업 등록</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>기업 팝업 스토어 등록</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/popups</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Part</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>req</td>
      <td>CreatePopupReq</td>
    </tr>
    <tr>
      <td>files</td>
      <td>MultipartFile[] (선택)</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "popupName": "팝업명",
  "popupAddress": "서울시 ...",
  "popupContent": "내용",
  "category": "패션",
  "totalPeople": 100,
  "popupStartDate": "2026-07-01",
  "popupEndDate": "2026-07-31"
}</code></pre>

- ✅ **HTTP 201 / code 4000** (초기 status=POPUP_START)
- ❌ **4001** 권한 없음

---

</details>

<details>
<summary><strong>[PATCH] 팝업 수정</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4006**
- ❌ **4007** 없음 / **4008** 소유자 아님

---

</details>

<details>
<summary><strong>[GET] 팝업 단일 조회</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4002** + GetPopupRes
- ❌ **4003** 없음

---

</details>

<details>
<summary><strong>[GET] 팝업 목록 (공개)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>설명</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>status</td>
      <td>필수. PopupStatus</td>
    </tr>
    <tr>
      <td>keyword</td>
      <td>선택</td>
    </tr>
    <tr>
      <td>page, size</td>
      <td>기본 0 / 10</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4004** + Page<GetPopupRes>

---

</details>

<details>
<summary><strong>[DELETE] 팝업 삭제</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>소프트 종료 (POPUP_END + 굿즈 STOCK 전환)</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/popups/{popupIdx}</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4009**

---

</details>

<details>
<summary><strong>[POST] 좋아요 토글</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}/likes</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4012**
- ❌ **4013** 팝업 없음

---

</details>

<details>
<summary><strong>[GET] 내 좋아요 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/likes/me</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>page, size</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

- ✅ **code 4015** + Page<GetPopupRes>

---

</details>

<details>
<summary><strong>[POST] 리뷰 등록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}/reviews</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "reviewTitle": "좋았어요",
  "reviewContent": "내용",
  "reviewRating": 5
}</code></pre>

<table>
  <thead>
    <tr>
      <th>필드</th>
      <th>제약</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>reviewTitle</td>
      <td>필수, max 100</td>
    </tr>
    <tr>
      <td>reviewContent</td>
      <td>필수, max 1000</td>
    </tr>
    <tr>
      <td>reviewRating</td>
      <td>필수, 1~5</td>
    </tr>
  </tbody>
</table>

- ✅ **HTTP 201 / code 4018**
- ❌ **4020** 구매이력 없음 / **4022** 중복

---

</details>

<details>
<summary><strong>[GET] 팝업 리뷰 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}/reviews</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4023** + Page<GetPopupReviewRes>

---

</details>

<details>
<summary><strong>[GET] 내 리뷰 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/reviews/me</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 4023** + Page<GetPopupReviewRes>

---

</details>

<details>
<summary><strong>[GET] 팝업별 예약 슬롯 목록 (공개)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/popups/{popupIdx}/reserves</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>keyword(선택), page, size</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

- ✅ **code 7008** + Page<GetReserveRes>

GetReserveRes: popupIdx, reserveIdx, reservePeople, reserveStartDate, reserveStartTime, reserveEndTime, getPopupRes

---

</details>

## 6 기업 팝업 관리

> 전체 /api/v1/company/popups/** → **ROLE_COMPANY**

<details>
<summary><strong>[GET] 내 팝업 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/company/popups</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>keyword, page, size</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

- ✅ **code 4004**

---

</details>

<details>
<summary><strong>[GET] 팝업 정산 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/company/popups/{popupIdx}/payouts</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 8000**

<pre><code data-language="json">{
  "success": true,
  "code": 8000,
  "message": "정산 조회에 성공했습니다.",
  "result": {
    "content": [
      { "revenue": 150000, "payoutDate": "2026-07-11" }
    ],
    "totalElements": 1,
    "totalPages": 1
  }
}</code></pre>

---

</details>

<details>
<summary><strong>[GET] 팝업 예약 목록 (기업)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/company/popups/{popupIdx}/reserves</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 7008** + Page<GetReserveRes>

---

</details>

<details>
<summary><strong>[GET] 팝업 주문 목록 (기업)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/company/popups/{popupIdx}/orders</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 6025** + Page<GetOrdersRes>

---

</details>

<details>
<summary><strong>[GET] 팝업 주문 상세 (기업)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/company/popups/{popupIdx}/orders/{ordersIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 6021** + GetOrdersRes

---

</details>

## 7 팝업 굿즈

<details>
<summary><strong>[POST] 굿즈 등록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/goods</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Part</th>
      <th>내용</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>req</td>
      <td>CreateGoodsReq</td>
    </tr>
    <tr>
      <td>files</td>
      <td>MultipartFile[] (필수)</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "popupIdx": 1,
  "goodsName": "티셔츠",
  "goodsPrice": 25000,
  "goodsAmount": 50,
  "goodsContent": "설명"
}</code></pre>

- ✅ **HTTP 201 / code 5000** (초기 GOODS_RESERVED)
- ❌ **5001** 스토어 없음 / **4008** 소유권

---

</details>

<details>
<summary><strong>[PATCH] 굿즈 수정</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/goods/{goodsIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>Content-Type</td>
      <td>multipart/form-data</td>
    </tr>
  </tbody>
</table>

- ✅ **code 5007**

---

</details>

<details>
<summary><strong>[GET] 굿즈 단일 조회</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/goods/{goodsIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

- ✅ **code 5003** + GetGoodsRes
- ❌ **5004** 없음

GetGoodsRes: popupName, goodsIdx, goodsName, goodsPrice, goodsContent, goodsAmount, goodsStatus, getGoodsImageResList

---

</details>

<details>
<summary><strong>[GET] 굿즈 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/goods</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>No</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>popupIdx, keyword, page, size</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

- ✅ **code 5005** + Page<GetGoodsRes>

---

</details>

<details>
<summary><strong>[DELETE] 굿즈 삭제</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/goods/{goodsIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 5010**

---

</details>

## 8 결제 / 주문

<details>
<summary><strong>[POST] 결제 검증 · 주문 생성</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>Iamport 결제 검증 후 예약/재고 주문 생성</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/orders</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "impUid": "imp_xxxxxxxx",
  "popupIdx": 1,
  "reserveIdx": 10
}</code></pre>

<table>
  <thead>
    <tr>
      <th>필드</th>
      <th>설명</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>impUid</td>
      <td>PortOne 결제 UID</td>
    </tr>
    <tr>
      <td>popupIdx</td>
      <td>팝업 인덱스</td>
    </tr>
    <tr>
      <td>reserveIdx</td>
      <td>있으면 예약구매, null이면 재고구매</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>구분</th>
      <th>규칙</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>예약</td>
      <td>품목당 1개, 배송비 0, 포인트 사용 불가, 5% 적립, RESERVE_READY</td>
    </tr>
    <tr>
      <td>재고</td>
      <td>재고한도, 배송비 2500, 포인트 3000↑ 사용, STOCK_READY</td>
    </tr>
  </tbody>
</table>

- ✅ **HTTP 201 / code 6000**

<pre><code data-language="json">{
  "success": true,
  "code": 6000,
  "message": "결제에 성공했습니다.",
  "result": { "ordersIdx": 1 }
}</code></pre>

- ❌ **6001~6007** 역할/회원/상품/한도/포인트/금액 오류

---

</details>

<details>
<summary><strong>[PATCH] 주문 상태 변경 (취소/확정/배송)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/orders/{orderIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER 또는 ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "popupIdx": 1,
  "status": "STOCK_CANCEL"
}</code></pre>

<table>
  <thead>
    <tr>
      <th>status</th>
      <th>동작</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>STOCK_CANCEL / RESERVE_CANCEL</td>
      <td>고객 취소·환불</td>
    </tr>
    <tr>
      <td>(그 외, 기업)</td>
      <td>배송 처리 → *_DELIVERY</td>
    </tr>
    <tr>
      <td>(그 외, 고객)</td>
      <td>구매 확정 → *_COMPLETE</td>
    </tr>
  </tbody>
</table>

- ✅ **code 6015**
- ❌ 취소 **6008~6014** / 확정 **6017~6020**

---

</details>

<details>
<summary><strong>[GET] 고객 주문 상세</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/orders/{ordersIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 6021** + GetOrdersRes

GetOrdersRes: ordersIdx, impUid, name, email, address, phoneNumber, usedPoint, totalPrice, orderStatus, deliveryCost, getOrdersDetailResList

---

</details>

<details>
<summary><strong>[GET] 고객 주문 목록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/orders</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

<table>
  <thead>
    <tr>
      <th>Query</th>
      <th>page, size</th>
    </tr>
  </thead>
  <tbody>
  </tbody>
</table>

- ✅ **code 6025** + Page<GetOrdersRes>

---

</details>

## 9 예약 / 대기열

<details>
<summary><strong>[POST] 예약 슬롯 등록</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/reserves</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

<pre><code data-language="json">{
  "popupIdx": 1,
  "reservePeople": 20,
  "reserveStartDate": "2026-07-15",
  "reserveStartTime": "2026-07-15T10:00:00",
  "reserveEndTime": "2026-07-15T12:00:00"
}</code></pre>

- ✅ **HTTP 201 / code 7000**

<pre><code data-language="json">{
  "success": true,
  "code": 7000,
  "message": "예약 등록에 성공했습니다.",
  "result": { "reserveIdx": 1 }
}</code></pre>

---

</details>

<details>
<summary><strong>[DELETE] 예약 슬롯 삭제</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/reserves/{reserveIdx}</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_COMPANY</td>
    </tr>
  </tbody>
</table>

- ✅ **code 7014**

---

</details>

<details>
<summary><strong>[GET] 예약 신청 (대기열 등록)</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>working/waiting 큐 등록, 승격 시 WTOKEN 발급</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>URL</td>
      <td>/api/v1/reserves/{reserveIdx}/enrollment</td>
    </tr>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 7005** + Set-Cookie WTOKEN (working 진입 시)
- ❌ **7006** / **7007** / Redis 900x

---

</details>

<details>
<summary><strong>[DELETE] 예약 취소 (대기열 이탈)</strong></summary>

<table>
  <thead>
    <tr>
      <th>URL</th>
      <th>/api/v1/reserves/{reserveIdx}/enrollment</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Auth</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
  </tbody>
</table>

- ✅ **code 7008**
- Side effect: WTOKEN 삭제, waiting 승격 시 WebSocket 알림

---

</details>

<details>
<summary><strong>[STOMP] 예약 상태 조회</strong></summary>

<table>
  <thead>
    <tr>
      <th>Description</th>
      <th>대기열 상태 실시간 조회</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Endpoint</td>
      <td>/ws (SockJS)</td>
    </tr>
    <tr>
      <td>Publish</td>
      <td>/pub/reserve/status</td>
    </tr>
    <tr>
      <td>Subscribe</td>
      <td>/user/queue/reserve/status</td>
    </tr>
    <tr>
      <td>Handshake</td>
      <td>Cookie ATOKEN 필요</td>
    </tr>
  </tbody>
</table>

**요청**

<pre><code data-language="json">{ "reserveIdx": 1 }</code></pre>

**응답 GetReserveQueueRes**

<pre><code data-language="json">{
  "waitingTotal": "3",
  "workingTotal": "10",
  "statusMessage": "대기 중입니다.",
  "access": 0,
  "wtoken": null
}</code></pre>

<table>
  <thead>
    <tr>
      <th>access</th>
      <th>의미</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>0</td>
      <td>대기</td>
    </tr>
    <tr>
      <td>1</td>
      <td>접속(결제 가능)</td>
    </tr>
    <tr>
      <td>2</td>
      <td>종료</td>
    </tr>
    <tr>
      <td>3</td>
      <td>마감</td>
    </tr>
  </tbody>
</table>

---

</details>

## 10 권한 매트릭스

<table>
  <thead>
    <tr>
      <th>Pattern</th>
      <th>Method</th>
      <th>Authority</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>/api/v1/auth/**</td>
      <td>*</td>
      <td>permitAll</td>
    </tr>
    <tr>
      <td>/api/v1/accounts/**</td>
      <td>*</td>
      <td>permitAll (principal 필요 API는 실질 인증)</td>
    </tr>
    <tr>
      <td>/api/v1/carts/**</td>
      <td>*</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/popups</td>
      <td>POST</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/popups/*</td>
      <td>PATCH, DELETE</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/popups, /api/v1/popups/*</td>
      <td>GET</td>
      <td>permitAll</td>
    </tr>
    <tr>
      <td>/api/v1/popups/*/likes</td>
      <td>POST</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/popups/likes/me</td>
      <td>GET</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/popups/*/reviews</td>
      <td>POST</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/popups/reviews/me</td>
      <td>GET</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/company/popups/**</td>
      <td>*</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/goods</td>
      <td>POST</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/goods/*</td>
      <td>PATCH, DELETE</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/goods, /api/v1/goods/*</td>
      <td>GET</td>
      <td>permitAll</td>
    </tr>
    <tr>
      <td>/api/v1/orders</td>
      <td>POST, GET</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/orders/*</td>
      <td>GET</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/api/v1/orders/*</td>
      <td>PATCH</td>
      <td>ROLE_CUSTOMER | ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/reserves</td>
      <td>POST</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/reserves/*</td>
      <td>DELETE</td>
      <td>ROLE_COMPANY</td>
    </tr>
    <tr>
      <td>/api/v1/reserves/*/enrollment</td>
      <td>GET, DELETE</td>
      <td>ROLE_CUSTOMER</td>
    </tr>
    <tr>
      <td>/ws/**, /pub/**, /user/**</td>
      <td>*</td>
      <td>permitAll</td>
    </tr>
  </tbody>
</table>

---

## 11 주요 코드 대역

<table>
  <thead>
    <tr>
      <th>대역</th>
      <th>도메인</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>1000~</td>
      <td>로그인/로그아웃</td>
    </tr>
    <tr>
      <td>2000~</td>
      <td>회원/인증</td>
    </tr>
    <tr>
      <td>3000~</td>
      <td>장바구니</td>
    </tr>
    <tr>
      <td>4000~</td>
      <td>팝업/좋아요/리뷰</td>
    </tr>
    <tr>
      <td>5000~</td>
      <td>굿즈</td>
    </tr>
    <tr>
      <td>6000~</td>
      <td>주문/결제</td>
    </tr>
    <tr>
      <td>7000~</td>
      <td>예약</td>
    </tr>
    <tr>
      <td>8000~</td>
      <td>정산</td>
    </tr>
    <tr>
      <td>9000~</td>
      <td>Redis 대기열</td>
    </tr>
    <tr>
      <td>300~</td>
      <td>JWT/공통 보안</td>
    </tr>
  </tbody>
</table>

---

> 변환 메모: 원문에 있는 API 정보와 예시를 템플릿의 접기 블록 형식으로 재배치했습니다. 원문에 없는 Base URL·Source 구현 경로는 확인 필요 또는 입력 경로로 표시했습니다.


---

## 소스 구현 검증

현재 API 경로와 권한은 백엔드 소스의 컨트롤러 매핑 및 SecurityConfig를 기준으로 확인했다.

<table>
  <thead>
    <tr>
      <th>영역</th>
      <th>소스 기준 구현 범위</th>
      <th>근거</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>계정·인증</td>
      <td>/api/v1/accounts, /api/v1/accounts/me, /api/v1/accounts/verification, /api/v1/accounts/id/find, /api/v1/accounts/password/find, /api/v1/accounts/password/reset</td>
      <td>account/controller/AccountController.java, JWT·OAuth2 필터</td>
    </tr>
    <tr>
      <td>팝업·굿즈·장바구니</td>
      <td>/api/v1/popups, /api/v1/goods, /api/v1/carts 및 리뷰·좋아요 하위 경로</td>
      <td>popup/controller, goods/controller, cart/controller</td>
    </tr>
    <tr>
      <td>주문·예약</td>
      <td>/api/v1/orders, /api/v1/reserves, STOMP /ws·/pub·/sub·/user</td>
      <td>orders/controller, reserve/controller, global/socket</td>
    </tr>
    <tr>
      <td>권한</td>
      <td>고객은 장바구니·주문·리뷰·좋아요·예약 참여, 기업은 팝업·굿즈·예약 관리 API 사용</td>
      <td>global/security/SecurityConfig.java</td>
    </tr>
  </tbody>
</table>

- 문서의 URL·HTTP 메서드·권한은 현재 소스 구현을 우선한다.
- 응답 코드와 DTO 필드는 소스 변경 시 함께 재검수해야 한다.



