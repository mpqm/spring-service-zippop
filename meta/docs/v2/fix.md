# ZIPPOP 프로젝트 버그 조사 및 수정 목록

조사일: 2026-11-01 (KST)  
기준: 현재 워킹트리의 소스코드. 기존 미커밋 UI 변경도 포함한다.  
최초 조사 범위: 조사 및 이 문서 작성. 최초 조사에서는 애플리케이션 코드 수정, 업무 데이터 변경, Docker 재배포, Git 커밋을 수행하지 않았다. 이후 요청에 따른 수정은 아래 이력으로 구분한다.

## 수정 이력

### BUG-01 / BUG-02 — 소스 수정 완료 (2026-11-01)

- 인증 메일 링크를 실제 `/api/v1/accounts/verification` 매핑으로 수정했다.
- `BaseConstant`의 localhost URL 상수와 클래스는 제거했다. [RequestUrlService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/web/RequestUrlService.java)가 주입된 `HttpServletRequest`와 `ServletUriComponentsBuilder`로 현재 요청의 scheme/host/port/context path를 사용한다.
- 인증 성공·실패 시 동일 요청 origin의 `/login?success=true`, `/login?error=true`로 이동한다. 백엔드는 임의의 별도 프론트엔드 포트를 추측하지 않는다. UI와 API가 다른 origin인 개발 환경에서는 프론트엔드 프록시를 통해 요청해야 동일한 공개 주소로 돌아온다.
- 비동기 메일 작업 전에 완성한 URL 문자열을 전달한다. 메일 작업 스레드에서 Servlet 요청에 접근하지 않는다. 이메일의 `+`, `@` 및 query 값을 인코딩한다.
- `server.forward-headers-strategy: framework`를 적용하고 Nginx의 API 프록시가 원래 Host(포트 포함)를 전달하도록 수정했다. Forwarded 헤더는 외부에 직접 노출된 클라이언트가 아니라 신뢰할 수 있는 프록시에서 정리·설정해야 한다.
- 검증: URL 생성·프록시·포트·컨텍스트 경로·메일 본문·인증 결과 리다이렉트·UUID 소비 테스트 10개를 추가했고, 기존 테스트 포함 총 36개가 통과했다. 변경된 Nginx 설정도 `nginx -t`로 통과했다. 실제 외부 메일 발송과 Docker 재배포는 수행하지 않았다. Git 커밋 없음.

## 조사 범위와 검증 수준

- 백엔드 Java 소스 142개, 프론트엔드 Vue/JS/CSS 소스 70개를 대상으로 파일 목록·호출 경로를 조사하고, 계정·인증·팝업·굿즈·장바구니·예약·주문·환불·정산·업로드·라우팅·배포 구성을 교차 검토했다.
- `실행 재현`: 현재 로컬 Docker 환경에서 요청을 보내 결과를 확인했다. 실행 중인 백엔드 이미지는 이번 조사에서 재빌드하지 않았으므로, 런타임 결과는 소스 근거와 함께 해석해야 한다.
- `소스 확정`: 실행 흐름에 필요한 검사나 처리가 없거나, 연결된 두 코드의 계약이 충돌함을 확인했다. 실제 결제·메일 발송·데이터 변경을 동반하는 E2E 재현을 완료했다는 뜻은 아니다.
- `추가 검증`: 결함 후보는 있지만 동시성 부하, 외부 서비스 또는 정책 확인이 더 필요한 항목이다. 확정 버그 수에는 포함하지 않는다.
- 전수조사는 조사 범위를 뜻하며 모든 입력·브라우저·외부 서비스 조합에 버그가 없음을 보증하지 않는다. GIF·화면설계서 작업은 중지 상태로 유지했다.

### 검증 결과

| 검증 | 결과 | 범위 및 한계 |
|---|---|---|
| `backend/gradlew.bat test --rerun-tasks --no-daemon` | 26개 통과, 실패·오류·스킵 0 | 10개 테스트 클래스. 외부 결제/메일/실제 Redis 대기열 통합 검증은 포함하지 않음 |
| `frontend/npm run test:unit -- --runInBand --watch=false` | 4개 suite, 41개 통과 | API 계약·스토어 경로·오류 처리·내비게이션 중심 |
| `frontend/npm run build` | 성공 | 번들 크기 및 Browserslist 경고 존재. 화면 동작 검증을 대신하지 않음 |
| 공개 팝업/예약/굿즈 조회 | HTTP 200 | 정상 조회 기준선 확인 |
| 고객 주문, 기업 주문·예약·정산 조회 | HTTP 200 | 로컬 시드 계정으로 인증 후 GET 요청. 결제·취소·수정은 호출하지 않음 |
| 인증 메일에 기재된 `/api/v1/auth/verify` | HTTP 404 | BUG-01 재현 |
| 올바른 `/api/v1/accounts/verification` + 잘못된 UUID | 로그인 오류 페이지로 이동 | 실제 매핑 확인. 계정 활성화는 발생하지 않음 |
| `Cookie: ATOKEN=invalid-token`으로 `/api/v1/accounts/me` | HTTP 500 | BUG-07 재현 |
| `status=WRONG`, `page=-1`로 팝업 목록 조회 | HTTP 500 | BUG-41 재현 |

우선순위: P1 = 인증·금전·재고·핵심 기능 장애, P2 = 일반 기능 오류·데이터 불일치, P3 = 경미한 표시·도구 문제. 아래 우선순위는 수정 순서 제안이며 적용 완료 표시가 아니다.

## 확정 버그 목록

### 계정·인증

#### BUG-01 [P1][수정 완료: 소스·회귀 테스트] 가입·복구 인증 메일 링크가 존재하지 않는 API를 가리킴

- 근거: [MailService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/mail/MailService.java) `sendSignupEmail()`은 `/api/v1/auth/verify`를 사용한다. [AccountController.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/controller/AccountController.java)의 실제 매핑은 `/api/v1/accounts/verification`이다.
- 영향/재현: 발송된 인증 링크를 열면 404가 발생하여 가입·계정 복구 인증을 완료할 수 없다. 잘못된 UUID를 사용한 읽기 요청으로 경로 불일치를 재현했다.
- 수정 방향: 메일 링크와 실제 컨트롤러 경로를 통일하고, 발송 본문의 URL을 검증하는 회귀 테스트를 추가한다.

#### BUG-02 [P1][수정 완료: 소스·회귀 테스트] 인증 링크·결과 리다이렉트가 localhost로 고정됨

- 최초 조사 근거: 삭제 전 `global/base/BaseConstant.java`의 서버·로그인 URL이 `localhost:8080/8081` 상수였다. 현재는 위 수정 이력대로 제거했다.
- 영향/재현: 외부 배포 환경에서 인증 메일을 클릭하면 방문자의 로컬 PC로 이동한다. 경로만 BUG-01처럼 고쳐도 배포 환경 인증은 실패한다.
- 수정 방향: 백엔드 공개 URL과 프론트엔드 URL을 환경 설정으로 분리한다.

#### BUG-03 [P1][소스 확정] 로그인 화면의 카카오·구글·네이버 버튼에 동작이 없음

- 근거: [LoginPage.vue](../../../frontend/src/pages/auth/LoginPage.vue)의 소셜 버튼 3개에 클릭 핸들러·링크가 없고 모두 `type="button"`이다.
- 영향/재현: 소셜 로그인 버튼을 눌러도 OAuth 인증 요청이 시작되지 않는다.
- 수정 방향: 지원하는 공급자의 OAuth 시작 URL을 연결하고, 미지원 공급자는 제공 범위를 명확하게 처리한다.

#### BUG-04 [P1][소스 확정] 구글·네이버 OAuth 사용자 매핑이 구현되지 않음

- 근거: [CustomOAuth2Service.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/security/oauth2/CustomOAuth2Service.java) `loadUser()`는 kakao만 처리한 뒤 `Objects.requireNonNull(oAuth2UserInfo)`를 호출한다.
- 영향/재현: 해당 공급자의 인증에 성공해 콜백에 진입해도 null 참조로 실패한다. BUG-03의 UI 연결만으로 해결되지 않는다.
- 수정 방향: 공급자별 사용자 정보 매핑과 실패 응답을 구현한다.

#### BUG-05 [P1][소스 확정] 최초 카카오 로그인 시 필수 회원 컬럼이 누락됨

- 근거: 같은 `CustomOAuth2Service.loadUser()`는 신규 고객에 이름·이메일·역할·포인트·이메일 인증 여부만 설정한다. [Customer.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/model/entity/Customer.java)는 `userId/password/phoneNumber/address/isInActive`를 NOT NULL로 선언한다.
- 영향/재현: 기존 고객이 없는 카카오 계정의 최초 가입 저장이 실패한다.
- 수정 방향: 소셜 회원 생성 모델 또는 추가 정보 입력 흐름을 정하고 필수 컬럼을 일관되게 채운다.

#### BUG-06 [P1][소스 확정] 고객·기업 간 동일 아이디 가입이 가능하지만 로그인은 고객을 우선 선택함

- 근거: [CustomerPolicy.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/policy/CustomerPolicy.java), [CompanyPolicy.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/policy/CompanyPolicy.java)는 자기 역할 테이블만 중복 확인한다. [CustomUserDetailService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/security/normal/CustomUserDetailService.java)는 고객 조회가 성공하면 기업 조회를 하지 않는다.
- 영향/재현: 양 역할에 같은 아이디가 있으면 기업 비밀번호가 맞아도 기업 계정으로 로그인할 수 없다. Refresh Token 키도 `userId`만 사용하여 역할이 충돌한다.
- 수정 방향: 로그인 ID를 전역 유일하게 만들거나 로그인·토큰 식별자에 역할을 일관되게 포함한다.

#### BUG-07 [P1][실행 재현] 잘못된 JWT가 인증 실패 대신 서버 500을 발생시킴

- 근거: [JwtFilter.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/security/filter/JwtFilter.java) 53행 부근의 `jwtService.isExpired()`가 try/catch 밖에 있다. [JwtService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/crypto/JwtService.java)는 만료 예외만 처리한다.
- 영향/재현: `ATOKEN=invalid-token`으로 내 정보 GET 요청 시 HTTP 500. 필터에서 발생한 파싱·서명 예외는 MVC 예외 처리기만으로 처리되지 않는다.
- 수정 방향: JWT 필터 전체 파싱 경계를 처리하고 401 오류 형식 및 쿠키 정리를 통일한다.

#### BUG-08 [P1][소스 확정] 비활성화·비밀번호 변경 후 기존 인증이 계속 유효함

- 근거: `JwtFilter`는 유효한 ATOKEN의 claim만으로 인증을 만든다. Refresh Token 분기에서도 `loadUserByUsername()` 반환 후 활성 상태를 검사하지 않는다. 계정 비활성화·비밀번호 변경 서비스는 토큰을 폐기하지 않는다.
- 영향/재현: 다른 브라우저에 남은 ATOKEN으로 비활성화 계정의 보호 API에 접근할 수 있고, RTOKEN 갱신에서도 계정 차단이 적용되지 않는다.
- 수정 방향: 계정 상태/인증 버전을 검사하고 비활성화·비밀번호 변경 시 세션 정책에 따라 토큰을 폐기한다.

#### BUG-09 [P2][소스 확정] 같은 아이디로 재가입하여 미인증 계정 인증 메일을 재전송할 수 없음

- 근거: [CustomerService.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/service/CustomerService.java), [CompanyService.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/service/CompanyService.java) `createAccount()`가 이메일 기반 복구 판단보다 먼저 ID 중복을 거부한다.
- 영향/재현: 동일 이메일·동일 아이디로 다시 제출하면 복구/재인증 분기에 도달하기 전에 중복 ID 오류가 난다. 별도 활성화 요청 API는 있지만 재가입 복구 분기의 동작과 충돌한다.
- 수정 방향: 기존 계정 소유 상태를 먼저 판단하고 본인 계정 ID 재사용과 타 계정 중복을 구분한다.

#### BUG-10 [P2][소스 확정] 이메일 인증 UUID가 역할별로 분리되지 않음

- 근거: [RedisEmailService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/redis/RedisEmailService.java)는 `emailVerify:<email>`로 저장한다. [EmailAuthFacade.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/application/EmailAuthFacade.java)는 요청의 role로 활성화 대상을 선택한다.
- 영향/재현: 같은 이메일로 고객·기업 인증을 요청하면 앞선 UUID가 덮어써진다. UUID 자체에 역할 연결이 없어 요청 role 변경 시 다른 역할 계정의 활성화에도 사용될 수 있다.
- 수정 방향: UUID를 이메일·역할·인증 목적에 연결하고 검증 후 원자적으로 소비한다.

### 결제·주문·환불

#### BUG-11 [P1][소스 확정] PG 결제 상태를 확인하지 않고 주문을 생성함

- 근거: [OrdersService.java](../../../backend/src/main/java/com/fiiiiive/zippop/orders/service/OrdersService.java) `createStockOrders()/createReserveOrders()`는 PG 조회 결과의 null·금액·customData를 검사하지만 결제 상태가 실제 결제 완료인지 확인하지 않는다.
- 영향/재현: 조회 가능한 미결제/취소 결제의 금액·상품 정보가 조건에 맞으면 주문 생성·재고 차감 경로에 진입한다. 실제 PG의 상태별 응답으로 추가 재현해야 한다.
- 수정 방향: PG 결제 완료 상태를 필수 검증하고 취소·가상계좌 입금 대기 등 상태를 구분한다.

#### BUG-12 [P1][소스 확정] 결제 식별자가 주문 요청 사용자에게 연결되어 있지 않음

- 근거: 위 메서드는 클라이언트가 보낸 `impUid`를 조회한 뒤 서버가 미리 발급한 주문번호·고객·장바구니와 대조하지 않는다.
- 영향/재현: 다른 사용자의 아직 처리되지 않은 결제 식별자를 알고 있으면 자기 계정 주문으로 등록을 시도할 수 있다. 결제 데이터 소유권 검증이 없다.
- 수정 방향: 결제 전에 서버 주문 의도를 만들고 주문번호·구매자·상품·금액을 PG 거래와 결합한다.

#### BUG-13 [P1][소스 확정] 동일 결제 재요청이 기존 정상 결제의 환불을 유발할 수 있음

- 근거: `OrdersService`는 기존 `impUid` 주문을 먼저 확인하지 않는다. [Orders.java](../../../backend/src/main/java/com/fiiiiive/zippop/orders/model/entity/Orders.java)는 `impUid` unique이고, 서비스의 예외 분기는 조회한 payment에 대해 롤백 환불 이벤트를 발행한다.
- 영향/재현: 이미 성공한 결제를 다시 제출하면 unique 충돌로 새 트랜잭션은 롤백되지만 기존 거래를 환불 대상으로 만들 수 있다. 기존 주문·차감 재고와 PG 상태가 어긋난다.
- 수정 방향: 결제 처리에 멱등성을 적용하고 기존 성공 주문 반환, 소유권 검사, 환불 대상 소유권을 분리한다.

#### BUG-14 [P1][소스 확정] 예약 결제 완료 직후 시간 만료·큐 이탈이 발생하면 환불 경로가 없음

- 근거: `createReserveOrders()`는 예약 시간·working 큐를 검사한 뒤에야 `paymentByImpUid()`를 호출한다. 사전 검사 실패 시 `payment == null`이라 환불 이벤트가 발행되지 않는다.
- 영향/재현: 브라우저 PG 결제 성공 후 검증 API 도착 전에 예약이 종료되거나 연결이 끊기면 결제만 완료되고 주문/자동 환불이 모두 누락될 수 있다.
- 수정 방향: 결제 의도 상태와 보상 처리를 서버에서 관리하여 유효성 실패 시 이미 결제된 거래도 정합하게 처리한다.

#### BUG-15 [P1][소스 확정] 주문 취소 DB 확정 후 PG 환불 실패를 복구할 수 없음

- 근거: [RefundEventListener.java](../../../backend/src/main/java/com/fiiiiive/zippop/orders/event/RefundEventListener.java) `handleRefundAfterCommit()`이 DB 커밋 뒤 PG를 호출한다. 재시도·실패 상태 저장·보상 처리가 없다.
- 영향/재현: PG 장애 시 주문은 취소되고 재고·포인트는 복구됐지만 돈은 환불되지 않은 상태가 남는다. 재취소는 이미 취소 상태라 차단된다.
- 수정 방향: 환불 요청/완료/실패 상태를 영속화하고 재시도 가능한 outbox 또는 작업 큐를 적용한다.

#### BUG-16 [P1][소스 확정] 동시 주문 취소가 재고·포인트를 중복 복구할 수 있음

- 근거: `OrdersService.cancelOrders()`는 주문 상태를 일반 조회한 뒤 검사한다. 주문 조회에 락·버전·원자적 상태 전이가 없다. 굿즈 락은 이후에 획득한다.
- 영향/재현: 두 요청이 READY 상태를 동시에 읽으면 둘 다 취소 검사를 통과하고 같은 주문의 재고 복구·환불 이벤트를 실행할 수 있다. 구체적인 발생 빈도는 부하 재현이 필요하다.
- 수정 방향: 주문을 잠그거나 상태 조건부 갱신을 수행해 취소 실행 주체를 하나로 제한한다.

#### BUG-17 [P1][소스 확정] 포인트 잔액 갱신이 동시 주문·취소에 안전하지 않음

- 근거: 고객을 일반 조회한 후 `customer.updatePoint(current ± amount)`로 저장한다. [CustomerRepository.java](../../../backend/src/main/java/com/fiiiiive/zippop/account/repository/CustomerRepository.java), Customer 엔티티에 해당 잔액의 락·버전 관리가 없다.
- 영향/재현: 같은 고객이 서로 다른 굿즈 주문을 동시에 결제하면 두 요청이 같은 잔액으로 사용 가능 검사를 통과할 수 있다. 포인트 중복 사용 또는 갱신 유실이 발생할 수 있다.
- 수정 방향: 잔액 조건부 원자 갱신 또는 고객 단위 락과 포인트 원장을 적용한다.

#### BUG-18 [P1][소스 확정] 예약 주문 취소 시 적립 포인트가 회수되지 않음

- 근거: `createReserveOrders()`는 구매액의 5%를 즉시 적립한다. `cancelOrders()`는 `usedPoint`만 복구하며 예약 주문의 usedPoint는 0이다.
- 영향/재현: 예약 주문을 결제했다 취소하면 환불 후에도 적립 포인트가 남는다.
- 수정 방향: 주문별 적립 포인트를 저장하고 취소 시 적립 취소를 처리한다. 적립 시점과 회수 정책도 통일한다.

#### BUG-19 [P2][소스 확정] 상품이 없는 결제 customData도 주문 생성 경로에 진입함

- 근거: 두 주문 생성 메서드가 `goodsMap.isEmpty()`를 검사하지 않는다. 빈 맵이면 상품 검증·상세 저장 반복문이 모두 생략된다.
- 영향/재현: customData `{}`와 계산에 맞는 금액의 PG 응답을 주면 주문 상세가 없는 주문을 만들 수 있다. 재고 주문은 배송비 2,500원만으로 금액 검사를 통과할 수 있다.
- 수정 방향: 최소 한 개의 유효 상품을 필수로 하고 서버에 저장한 주문 항목과 비교한다.

#### BUG-20 [P2][소스 확정] 주문 당시 수량·단가·배송지가 보존되지 않음

- 근거: [OrdersDetail.java](../../../backend/src/main/java/com/fiiiiive/zippop/orders/model/entity/OrdersDetail.java)는 수량·당시 단가 없이 합계와 현재 Goods 참조를 저장한다. [OrdersDetailPage.vue](../../../frontend/src/pages/orders/OrdersDetailPage.vue)는 `eachPrice / 현재 goodsPrice`로 수량을 계산한다. Orders DTO의 주소도 현재 Customer 주소를 사용한다.
- 영향/재현: 주문 후 굿즈 가격을 바꾸면 과거 구매 수량이 소수 또는 다른 수량으로 표시되고, 프로필 주소 변경은 과거 주문 배송지까지 바꿔 표시한다.
- 수정 방향: 주문 시점의 단가·수량·상품 정보·수령인·배송지를 스냅샷으로 저장하고 조회한다.

### 예약·대기열

#### BUG-21 [P1][소스 확정] Redis 큐 생성 시 빈 Sorted Set이 사라져 만료 시간이 적용되지 않음

- 근거: [RedisQueueService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/redis/RedisQueueService.java) `createQueue()`는 `start` 멤버를 넣고 즉시 지운 뒤 expire를 호출한다. 마지막 멤버 제거로 키가 없어지므로, 이후 실제 등록으로 생긴 키에는 이 TTL이 없다.
- 영향/재현: 종료 시각 TTL을 설정했다는 로그와 실제 큐 수명이 다르다. 스케줄러 정리에 누락된 큐는 계속 남을 수 있다.
- 수정 방향: 실제 첫 멤버 등록 시 만료를 적용하거나 예약 종료 epoch 기준으로 등록·만료를 원자적으로 처리한다.

#### BUG-22 [P1][소스 확정] 표시된 예약 유효시간 10분이 서버에서 강제되지 않음

- 근거: [ReserveSystemPage.vue](../../../frontend/src/pages/reserve/ReserveSystemPage.vue)는 10분 유효시간을 표시하지만, `OrdersService.createReserveOrders()`는 working 큐 소속과 예약 종료 시간만 확인한다. [AccessControlService.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/security/normal/AccessControlService.java)의 접근 검사도 SecurityConfig에 연결되어 있지 않고 WTOKEN 서명·만료를 검증하지 않는다.
- 영향/재현: 접속 10분이 지나 WTOKEN이 만료돼도 큐에 남아 있으면 주문 검증을 시도할 수 있다. 사용자별 체류 제한과 화면 안내가 일치하지 않는다.
- 수정 방향: 예약·고객에 결합한 입장 권한과 만료를 모든 예약 보호 API에서 검사하고, 만료 사용자 제거·승격을 수행한다.

#### BUG-23 [P2][소스 확정] 자정을 넘는 예약은 종료 후 큐 정리·이탈 처리가 누락됨

- 근거: [ReserveService.java](../../../backend/src/main/java/com/fiiiiive/zippop/reserve/service/ReserveService.java) `managementReserveQueue()`와 [WebSocketDisconnectHandler.java](../../../backend/src/main/java/com/fiiiiive/zippop/global/socket/WebSocketDisconnectHandler.java)는 `startDate == 오늘`인 예약만 조사한다. 등록은 시작일과 종료일이 다른 예약도 허용한다.
- 영향/재현: 전날 시작해 오늘 종료되는 예약은 오늘의 복구·종료 정리·소켓 이탈 검색 대상에서 빠진다. BUG-21과 결합하면 큐 잔존이 지속된다.
- 수정 방향: 시작일 일치 대신 진행/만료 구간으로 조회하고 종료 큐를 일관되게 정리한다.

#### BUG-24 [P2][소스 확정] 소켓 하나가 끊겨도 해당 사용자의 다른 예약 접속을 제거할 수 있음

- 근거: `WebSocketDisconnectHandler`는 세션의 예약 ID 없이 이메일로 오늘의 모든 예약을 순회하고 첫 번째 멤버십을 지운 뒤 return한다. 여러 예약 등록을 막는 서버 검사도 없다.
- 영향/재현: 동일 고객의 다중 탭·다른 예약 소켓에서 한 연결만 끊어도 다른 탭의 큐 항목이 삭제될 수 있다. 여러 큐에 남으면 하나만 제거되고 나머지는 잔존한다.
- 수정 방향: 소켓 세션을 예약 ID에 연결하고, 동일 예약 다중 연결 수 또는 명시적인 단일 접속 정책을 관리한다.

#### BUG-25 [P1][소스 확정] 남은 인원을 전부 예약에 배정하면 엔티티 검증과 충돌함

- 근거: `ReserveService.createReserve()`는 배정 후 `Popup.totalPeople`에서 인원을 차감한다. [Popup.java](../../../backend/src/main/java/com/fiiiiive/zippop/popup/model/entity/Popup.java) 71행의 `@Positive`는 0을 허용하지 않는다.
- 영향/재현: 남은 인원 1명에 1명짜리 예약을 만들면 잔여 인원 0이 되어 정상적인 전량 배정이 flush/commit 검증에서 실패할 수 있다. 기존 서비스 mock 테스트는 JPA 검증을 실행하지 않는다.
- 수정 방향: 잔여 인원은 0 이상을 허용하고 총 정원과 잔여 정원을 분리한다.

#### BUG-26 [P1][소스 확정] 동시 예약 등록으로 팝업 정원을 초과 배정할 수 있음

- 근거: `ReserveService.createReserve()`는 팝업을 일반 조회하고 남은 인원을 비교한 뒤 읽은 값에서 차감한다. 팝업 조회 락·버전·조건부 차감이 없다.
- 영향/재현: 잔여 10명을 두 요청이 동시에 읽고 각각 8명 슬롯을 생성하면 슬롯 총합 16명이지만 팝업 잔여값은 2명으로 저장될 수 있다.
- 수정 방향: 팝업 정원 배정을 잠그거나 `잔여 >= 요청` 조건의 원자적 차감으로 전환한다.

#### BUG-27 [P2][소스 확정] 운영 기간과 맞지 않는 팝업·예약 날짜를 서버가 허용함

- 근거: [CreatePopupReq.java](../../../backend/src/main/java/com/fiiiiive/zippop/popup/model/dto/CreatePopupReq.java), UpdatePopupReq는 날짜 null만 검사한다. Popup 생성·수정은 종료일 ≥ 시작일을 검사하지 않는다. 예약 생성은 예약 자체의 시작·종료 순서만 확인하고 팝업 운영 기간 안인지 확인하지 않는다.
- 영향/재현: 역전된 운영 기간 또는 팝업 종료일 뒤의 예약을 API로 등록할 수 있다. 프론트엔드 수정 폼의 일부 검사로 서버 무결성이 보장되지 않는다.
- 수정 방향: 운영 기간, 예약 가능 구간, 날짜 변경 시 기존 예약과의 충돌을 서버에서 검사한다.

#### BUG-28 [P1][소스 확정] 종료/삭제 처리한 팝업의 기존 예약에 계속 입장 가능함

- 근거: [PopupService.java](../../../backend/src/main/java/com/fiiiiive/zippop/popup/service/PopupService.java) `deletePopup()`는 상태·굿즈만 바꾸며 예약/큐를 종료하지 않는다. `ReserveService.enrollReserve()`는 예약 시간·인원만 검사하고 팝업 상태를 검사하지 않는다.
- 영향/재현: 활성 슬롯이 있는 팝업을 종료한 뒤 기존 예약 URL 또는 enrollment API로 접근하면 큐 입장이 가능하다. 이후 상품 상태는 재고로 바뀌어 예약 구매와도 충돌한다.
- 수정 방향: 팝업 종료 정책에 따라 예약·큐를 닫고 입장·주문 검증에서 팝업 상태를 함께 확인한다.

### 팝업·굿즈

#### BUG-29 [P2][소스 확정] 종료 팝업에 새로 등록한 굿즈가 재고 상품이 되지 않음

- 근거: [GoodsService.java](../../../backend/src/main/java/com/fiiiiive/zippop/goods/service/GoodsService.java)는 종료 팝업 등록을 제한하지 않는다. [Goods.java](../../../backend/src/main/java/com/fiiiiive/zippop/goods/model/entity/Goods.java) `create()`는 항상 `GOODS_RESERVED`를 지정한다.
- 영향/재현: 재고 마켓 팝업에 굿즈를 추가하면 목록·장바구니에서는 보여도 재고 주문의 상태 검증에서 실패한다.
- 수정 방향: 팝업 상태에 맞는 굿즈 상태를 적용하거나 허용하지 않는 등록을 서버에서 거부한다.

#### BUG-30 [P2][소스 확정] 팝업 종료 스케줄러가 당일 오전에 종료시키며 지난 종료일을 보정하지 않음

- 근거: [PopupScheduler.java](../../../backend/src/main/java/com/fiiiiive/zippop/popup/scheduler/PopupScheduler.java)는 매일 09:00에 실행한다. `PopupService.updatePopupStatus()`는 종료일이 오늘과 같은 것만 조회한다.
- 영향/재현: 화면의 종료일 당일 23:59:59 기준과 달리 오전 9시에 종료될 수 있다. 서버 중단으로 당일 작업을 놓치면 재시작 후에도 지난 종료일 데이터가 진행 상태로 남는다.
- 수정 방향: 종료일 포함 여부를 정하고 경과한 모든 활성 팝업을 멱등적으로 전환한다.

### 프론트엔드

#### BUG-31 [P2][소스 확정] 검색어를 URL 인코딩하지 않아 특수문자 검색이 깨짐

- 근거: [popupStore.js](../../../frontend/src/stores/popupStore.js), [goodsStore.js](../../../frontend/src/stores/goodsStore.js), reserveStore.js가 검색어를 URL 문자열에 직접 삽입한다.
- 영향/재현: `A&B`, `C#`, `a+b` 등의 입력이 다른 파라미터·fragment·공백으로 해석되어 원래 검색어가 서버로 전달되지 않는다.
- 수정 방향: axios `params` 또는 URLSearchParams를 사용하고 특수문자 검색 계약을 검사한다.

#### BUG-32 [P2][소스 확정] 새 검색을 해도 현재 페이지를 유지해 결과가 없다고 표시될 수 있음

- 근거: [PopupMainPage.vue](../../../frontend/src/pages/popup/PopupMainPage.vue), GoodsMainPage, GoodsDetailPage1, PopupDetailPage, 기업 목록 페이지들의 검색 버튼이 기본 인자로 조회를 호출한다. `currentPage = 0`은 별도 초기화 버튼 경로에만 있다.
- 영향/재현: 전체 목록 3페이지에서 결과가 1페이지뿐인 키워드를 검색하면 3페이지를 요청하여 빈 결과를 보여준다.
- 수정 방향: 검색 제출과 페이지 이동을 분리하고 새로운 검색 조건 제출 시 0페이지로 이동한다.

#### BUG-33 [P2][소스 확정] 좋아요 목록 페이지 수를 갱신하지 않음

- 근거: `popupStore.getMyLikedPopups()`는 `likeList`만 저장한다. [LikeManagePage.vue](../../../frontend/src/pages/mypage/customer/LikeManagePage.vue)는 popupStore의 `totalPages/totalElements`를 사용한다.
- 영향/재현: 좋아요가 많아도 페이지 버튼이 없거나, 직전에 조회한 팝업·리뷰 목록의 페이지 수가 표시된다. 좋아요 캐시도 현재 페이지 항목으로 덮어써져 다른 팝업의 활성 표시가 달라질 수 있다.
- 수정 방향: 좋아요 페이지 메타데이터와 전체 좋아요 ID 캐시를 분리하여 갱신한다.

#### BUG-34 [P2][소스 확정] 상세 페이지의 기업 좋아요 차단 검사가 없는 속성을 참조함

- 근거: [PopupDetailPage.vue](../../../frontend/src/pages/popup/PopupDetailPage.vue), [GoodsDetailPage1.vue](../../../frontend/src/pages/goods/GoodsDetailPage1.vue)는 `authStore.userInfo?.role`을 조회하지만 authStore에는 userInfo가 없다. 실제 역할은 accountStore에 있다.
- 영향/재현: 기업 사용자의 좋아요 클릭이 로컬 안내에서 차단되지 않고 API 403까지 진행된다.
- 수정 방향: 공통 역할 검사로 통일하고 기업 사용자 동작을 검증한다.

#### BUG-35 [P2][소스 확정] 회원정보 폼 입력이 저장 전부터 전역 계정 정보에 반영됨

- 근거: [EditProfilePage.vue](../../../frontend/src/pages/mypage/common/EditProfilePage.vue) `getAccount()`는 `userInfo.value = accountStore.userInfo`로 같은 객체를 참조하고 이름·전화번호·사업자번호 input을 해당 객체에 직접 v-model 연결한다.
- 영향/재현: 이름 등을 바꾼 뒤 저장하지 않거나 저장 요청이 실패해도 헤더 등 다른 컴포넌트가 보는 계정 정보는 이미 바뀐다. 서버에 저장되지 않은 입력과 실제 회원 정보가 일치하지 않는다.
- 수정 방향: 폼 전용 복사본에 입력하고 저장 성공 시 서버 응답 또는 재조회 결과로 전역 상태를 갱신한다.

#### BUG-36 [P1][소스 확정] 예약 결제 주문번호가 1,000개 값만 순환함

- 근거: `ReserveSystemPage.payment()`는 `merchant_uid: "order_no_" + new Date().getMilliseconds()`를 사용한다.
- 영향/재현: 밀리초 값은 0~999이므로 서로 다른 사용자·시간의 결제 주문번호가 반복된다. PG 주문번호 중복으로 결제가 실패할 수 있다.
- 수정 방향: 서버에서 전역 유일한 결제 주문번호를 발급한다.

#### BUG-37 [P2][소스 확정] 시간이 지나도 예약 버튼의 활성 상태가 자동으로 바뀌지 않음

- 근거: [PopupCard.vue](../../../frontend/src/components/PopupCard.vue)의 computed는 `Date.now()`를 사용하지만 반응형 시계가 없다. [ReserveList.vue](../../../frontend/src/components/ReserveList.vue)도 부모 렌더를 갱신할 시간 상태가 없다.
- 영향/재현: 페이지를 열린 채로 시작 시각을 넘겨도 예약 버튼이 비활성 상태로 남거나 종료 뒤에도 활성 스타일이 남는다. 자식 카운트다운 갱신은 부모 버튼 계산을 갱신하지 않는다.
- 수정 방향: 공통 반응형 현재 시각 또는 다음 경계 시각 예약을 적용하고 서버 검증도 유지한다.

#### BUG-38 [P2][소스 확정] 로그아웃 후 주문·장바구니·결제 준비 정보가 남음

- 근거: [authStore.js](../../../frontend/src/stores/authStore.js) `clearSession()`은 인증·계정·좋아요만 지운다. cartStore/ordersStore/reserveStore는 sessionStorage에 영속화되고 paymentData도 그대로 남는다.
- 영향/재현: A가 결제 확인 화면까지 진행 후 로그아웃하고 B가 같은 탭에서 로그인하면 A의 결제 준비 상품을 `/orders`에서 다시 볼 수 있다. 이전 예약 access 상태도 잔존한다.
- 수정 방향: 로그아웃·사용자 변경 시 사용자 소유 store 전체를 초기화하고 결제 준비 데이터의 사용자 연결을 검사한다.

#### BUG-39 [P2][소스 확정] 수량 1에서 마이너스를 눌러 삭제하는 프론트엔드 분기가 실행되지 않음

- 근거: [CartManagePage2.vue](../../../frontend/src/pages/mypage/customer/CartManagePage2.vue)는 감소 API 성공 후에만 수량 1 삭제 분기를 실행한다. [CartItem.java](../../../backend/src/main/java/com/fiiiiive/zippop/cart/model/entity/CartItem.java) `decrease()`는 수량 1이면 먼저 오류를 던진다.
- 영향/재현: 수량 1에서 마이너스 클릭 시 삭제 대신 오류 토스트가 발생한다. 별도 삭제 버튼은 정상 경로다.
- 수정 방향: UI에서 수량 1 감소를 막거나, 감소 요청 전에 삭제할지 판단하여 계약을 통일한다.

### 빌드·입력 검증

#### BUG-40 [P2][소스 확정] Gradle Docker 작업이 존재하지 않는 cicd 경로를 사용함

- 근거: [build.gradle](../../../backend/build.gradle)의 Dockerfile·Compose 작업이 `../cicd/...`를 참조한다. 현재 파일은 `meta/infra/`에 있고 cicd 폴더는 없다.
- 영향/재현: 해당 Gradle Docker 태스크를 사용하면 파일을 찾지 못한다. 현재 수동 `docker compose -f meta/infra/docker-compose.yml` 흐름과 별개다.
- 수정 방향: 유지할 배포 진입점을 정하고 Gradle 작업의 경로·플랫폼 의존 명령을 현재 저장소 구조에 맞춘다.

#### BUG-41 [P2][실행 재현] 잘못된 목록 조건이 검증 오류 대신 HTTP 500으로 반환됨

- 근거: [PopupService.java](../../../backend/src/main/java/com/fiiiiive/zippop/popup/service/PopupService.java)의 `PopupStatus.valueOf(status)`, 서비스들의 `PageRequest.of(page, size)`에 입력 경계 검사가 없다. 예외 처리기는 해당 IllegalArgumentException을 일반 서버 오류로 처리한다.
- 영향/재현: `GET /api/v1/popups?status=WRONG&page=0&size=1`, `status=POPUP_START&page=-1&size=1` 모두 500 재현. 프론트엔드는 500 응답 시 오류 페이지로 이동한다.
- 수정 방향: enum 변환·페이지 범위·최대 페이지 크기를 요청 단계에서 검증하고 400으로 반환한다.

## 추가 검증이 필요한 항목

확정 버그와 별도 관리한다. 아래는 실제 조건·정책·동시 요청 결과까지 확인한 후 수정 범위를 확정해야 한다.

| ID | 우선순위 | 근거 및 확인할 내용 |
|---|---|---|
| CHECK-01 | P1 | LocalFileUploadService는 원본 파일명을 경로에 결합하면서 normalize/저장 루트 포함 여부·허용 확장자·실제 이미지 형식을 검사하지 않는다. S3도 파일 형식 검사가 없다. 조작된 파일명·HTML/SVG 업로드와 정적 제공 방식까지 검증 필요. 정상 이미지로만 UI를 검사해서는 확인 불가 |
| CHECK-02 | P1 | CustomAuthenticationInterceptor는 STOMP CONNECT만 검사한다. SEND/SUBSCRIBE 목적지 권한, 다른 사용자 큐 구독 차단, 장시간 연결 중 ATOKEN 만료 처리가 별도 검증되지 않았다. 실제 2사용자 소켓으로 접근 범위 확인 필요 |
| CHECK-03 | P2 | PayoutService는 enum 필드 조회에 문자열 status를 넘기는 OrdersRepository 메서드를 사용한다. 실제 정산 스케줄러 실행 시 Hibernate 파라미터 바인딩을 확인해야 한다. 정산 GET 200은 생성 스케줄러 정상 동작의 증거가 아님 |
| CHECK-04 | P2 | 재고 구매 UI는 5% 포인트 적립을 안내하지만 createStockOrders에는 포인트 차감만 있다. 적립 정책을 확정하고 예약 구매와 통일할지 확인 필요 |
| CHECK-05 | P1 | PopupLike의 고객·팝업 복합 유일 제약과 Popup의 버전/원자 증감이 없다. 동시 좋아요 토글 시 중복 레코드 및 likeCount 불일치 가능성을 부하 테스트로 확인 필요 |
| CHECK-06 | P1 | createReserveOrders의 잔여 인원 검사와 decreaseTotalPeople이 조건부 차감으로 연결되지 않는다. 서로 다른 상품 구매 또는 동일 사용자의 동시 결제에서 정원 초과/중복 예약 구매가 가능한지 정책과 부하 재현 필요 |
| CHECK-07 | P2 | GoodsService.deleteGoods는 CartItem/OrdersDetail에서 참조하는 굿즈도 물리 삭제를 시도한다. 실제 DB FK가 설정된 데이터에서 삭제 거부(500) 여부를 확인하고, 판매 이력이 있는 상품은 비활성화할지 정책 결정 필요 |
| CHECK-08 | P2 | CreateAccountReq의 crn에는 기업 역할별 필수·길이 검증이 없지만 Company.crn은 NOT NULL/길이 15다. null/빈 문자열/긴 입력에 대한 가입·수정 응답 검증 필요. 실제 가입을 호출하면 메일·계정 생성이 발생하므로 이번에는 수행하지 않음 |
| CHECK-09 | P2 | 재고·예약 결제 금액이 int 누적이고 payment.getAmount().intValue()로 변환된다. 매우 큰 금액·수량에서 overflow/절삭 가능성 확인 및 금액 상한 정책 필요 |
| CHECK-10 | P2 | ReserveSystemPage는 취소 시 소켓 disconnect 후 DELETE를 요청한다. 서버 disconnect 정리와 명시적 취소가 경쟁할 때 WTOKEN 블랙리스트/쿠키 삭제 또는 승격이 누락되는지 통합 검증 필요 |

## 우선 수정 순서 및 완료 기준

1. 인증: BUG-01~08. 가입→메일 링크→로그인→계정 차단→기존 토큰 거절까지 검증한다.
2. 결제: BUG-11~18, BUG-36. PG mock의 paid/ready/cancelled·동일 결제 재요청·환불 실패·동시 취소를 포함한다. 실제 결제 승인은 별도 테스트 환경에서 수행한다.
3. 예약: BUG-21~28. 실제 Redis를 사용해 만료·정원 전량 배정·동시 등록·자정 경계·다중 탭·팝업 종료를 검증한다.
4. 화면 및 데이터: BUG-19~20, BUG-29~35, BUG-37~41. 가격/주소 변경 전후 주문 이력, 새 검색 첫 페이지, 특수문자, 로그아웃 후 계정 교체를 검증한다.
5. 추가 검증 항목은 결과를 확보한 후 확정 목록으로 승격하거나 근거와 함께 제외한다.

## 제외한 오탐 및 조사 한계

- 서비스의 정렬 필드 `id`가 엔티티 필드 `idx`와 다르다는 이유만으로 장애로 판정하지 않았다. 현재 고객 주문·기업 주문·공개 예약·기업 예약 GET은 정상 응답을 확인했다.
- GoodsRepository의 비관적 락은 이미 존재한다. 단순히 '굿즈 재고 락이 없다'는 항목은 포함하지 않았다. 주문·고객 잔액·팝업 정원은 별도 자원의 동시성 문제다.
- 기존 미커밋 UI 변경은 보존했다. 이 문서는 해당 변경의 커밋·롤백·수정을 요청하지 않는다.
- 실제 결제/환불 승인, 이메일 발송, 회원 생성·비활성화, 상품/예약 생성·삭제, 운영 데이터 변경을 통한 재현은 수행하지 않았다. 동시성 결함의 발생 빈도는 계측하지 않았다.
- 최초 조사에서 추가한 파일은 `meta/docs/v2/fix.md`다. 이후 코드 수정은 수정 이력에 기록하며 Git 커밋은 하지 않는다.
