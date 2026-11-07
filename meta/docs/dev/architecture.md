# ZIPPOP 백엔드 패키지 아키텍처

## 1. 문서 목적

이 문서는 `backend/src/main/java/com/fiiiiive/zippop`의 현재 패키지 구조를 평가하고, 기능 추가와 리팩터링 시 적용할 의존 방향 및 목표 패키지 구조를 정의한다.

현재 프로젝트는 기능별 최상위 패키지를 사용하는 모놀리식 애플리케이션이다. 기능별 패키징 자체는 유지하되, 각 기능이 다른 기능의 Entity, Repository, DTO를 직접 참조하면서 발생하는 결합을 단계적으로 줄이는 것을 목표로 한다.

이 문서는 즉시 전체 구조를 교체하라는 의미가 아니다. 신규 기능부터 기준을 적용하고, 기존 기능은 테스트가 확보된 범위에서 단계적으로 이동한다.

## 2. 현재 구조 요약

```text
com.fiiiiive.zippop
├─ account
├─ cart
├─ global
├─ goods
├─ orders
├─ payout
├─ popup
└─ reserve
```

기능별 최상위 패키지는 구분되어 있지만 실질적인 모듈 경계는 약하다.

- `popup`이 account, goods, cart, orders, reserve, payout을 폭넓게 참조한다.
- `orders`가 customer, goods, popup, reserve 저장소와 외부 결제 모듈을 직접 사용한다.
- `reserve`의 대기열, Redis, WebSocket, 접근 제어 구현이 `global`에 분산되어 있다.
- 여러 응답 DTO가 다른 기능의 응답 DTO를 그대로 포함한다.
- JPA 양방향 관계가 실제 업무 경계보다 넓게 설정되어 있다.
- `global`이 기술 공통 기능뿐 아니라 예약 업무 기능과 모든 도메인의 상태·오류 코드까지 보유한다.

따라서 현재 구조는 `package-by-feature` 형태를 갖췄지만, 각 패키지를 독립적인 모듈로 보기는 어렵다.

## 3. 전체 평가

| 패키지 | 평가 | 현재 상태 |
|---|---:|---|
| `account` | 7/10 | Facade와 공통 계정·프로필 서비스가 분리되어 비교적 안정적이다. |
| `goods` | 6.5/10 | 단순하고 응집도가 높지만 Popup과 JPA 관계에 직접 결합되어 있다. |
| `cart` | 6/10 | 기능 경계는 명확하지만 Goods·Popup Entity/DTO를 직접 사용한다. |
| `payout` | 6/10 | 작고 단순하지만 OrdersRepository를 직접 사용한다. |
| `global` | 5.5/10 | 공통 기능과 예약·계정 업무 기능이 혼재한다. |
| `popup` | 4.5/10 | 주문·예약·정산 조회까지 담당하는 중심 허브가 되었다. |
| `reserve` | 4.5/10 | 일정 CRUD, 대기열, 입장, Redis, WebSocket 책임이 집중되어 있다. |
| `orders` | 4/10 | 결제, 주문, 재고, 포인트, 환불, 상태 변경과 조회가 집중되어 있다. |

점수는 코드 품질의 절대 평가가 아니라 구조적 응집도와 결합도를 기준으로 한 상대 평가다.

## 4. 패키지별 평가

### 4.1 account

`AccountFacade`, `AccountService`, `CustomerProfileService`, `CompanyProfileService`로 공통 계정과 역할별 프로필 책임이 분리되어 있다. 현재 패키지 중 개선 방향이 가장 선명하다.

보완할 사항은 다음과 같다.

- `global.security`에 있는 계정 조회 기반 인증 구현을 account의 infrastructure 영역으로 이동한다.
- `model` 아래에 혼재한 Entity와 DTO를 `domain`과 `api` 또는 `application/dto`로 분리한다.
- Facade는 유스케이스 조합과 트랜잭션 경계를 담당하고, Service는 단일 도메인 규칙을 담당하도록 기준을 유지한다.
- 소셜 로그인 구현 전까지 미사용 공급자별 클래스가 핵심 계정 흐름과 결합되지 않도록 한다.

권장 구조는 다음과 같다.

```text
account
├─ api
├─ application
├─ domain
│  ├─ Account
│  ├─ Customer
│  └─ Company
└─ infrastructure
   ├─ persistence
   └─ security
```

### 4.2 goods

GoodsService의 CRUD 책임은 비교적 명확하고 서비스 크기도 과도하지 않다. Goods가 Popup의 하위 자원이라는 점에서 Popup 조회와 소유권 확인 의존은 업무상 자연스럽다.

다만 다음 결합은 줄일 필요가 있다.

- GoodsService가 PopupRepository와 PopupPolicy를 직접 참조한다.
- Goods Entity가 CartItem과 OrdersDetail의 역방향 컬렉션을 보유한다.
- Goods 응답 DTO를 Cart와 Orders가 직접 재사용한다.

DB 외래키가 존재한다고 모든 방향의 JPA 관계가 필요한 것은 아니다. Goods에서 사용하지 않는 역방향 컬렉션은 제거하고, 필요한 조회는 전용 Repository 쿼리로 처리한다.

Popup 소유권 확인은 다음과 같은 좁은 인터페이스를 통해 제공하는 방식을 권장한다.

```java
public interface PopupOwnershipReader {
    PopupOwnership getOwnedPopup(Long popupId, Long companyAccountId);
}
```

### 4.3 cart

장바구니와 장바구니 항목이라는 기능 경계는 명확하다. 하지만 Cart Entity와 응답 DTO가 Customer, Goods, Popup, PopupImage의 구체 타입을 직접 참조한다.

특히 다른 기능의 응답 DTO를 재사용하면 해당 기능의 API 변경이 Cart API의 변경으로 전파된다. Cart 응답에는 Cart가 실제로 제공하는 필드만 포함한 전용 DTO를 사용한다.

```text
CartItemResponse
├─ goodsId
├─ goodsName
├─ unitPrice
├─ quantity
└─ thumbnailUrl
```

장바구니에 저장하는 가격이 결제 시점의 현재 가격과 다를 수 있으므로, Cart의 가격은 참고용 스냅샷인지 확정 견적인지 의미를 명확히 해야 한다. 결제 확정 금액의 진실 원천은 Orders의 서버 결제 의도여야 한다.

### 4.4 popup

현재 가장 많은 기능이 모이는 패키지다. Popup 자체 CRUD와 좋아요·리뷰 외에 기업 기준 주문·예약·정산 조회까지 Popup 서비스가 담당한다.

`CompanyPopupService`는 이름과 달리 기업 마이페이지 조회 Facade에 가깝다. 다음 책임을 Popup에서 분리해야 한다.

- 기업 정산 목록 조회
- 기업 예약 목록 조회
- 기업 주문 목록 및 상세 조회

권장 위치는 다음 중 하나다.

```text
company/application/CompanyDashboardFacade
```

또는

```text
mypage/application/CompanyMypageQueryService
```

Popup Entity가 Goods, Cart, Reserve, Payout 등의 양방향 컬렉션을 모두 보유하면 Popup 변경 트랜잭션의 범위와 실제 업무 경계가 불명확해진다. Popup 수정에 필요하지 않은 역방향 관계는 제거한다.

리뷰 작성 자격을 확인하기 위해 OrdersDetailRepository를 PopupService가 직접 사용하는 구조도 결합도가 높다. 다음과 같은 조회 포트를 Popup이 의존하고 Orders가 구현하는 형태가 더 적절하다.

```java
public interface PopupPurchaseHistoryReader {
    boolean hasCompletedPurchase(Long customerId, Long popupId);
}
```

### 4.5 reserve

ReserveService에는 다음 책임이 함께 존재한다.

- 예약 일정 등록·수정·삭제
- 예약 목록과 상태 조회
- 사용자 대기열 등록·취소
- 작업열 입장과 순번 계산
- Redis 키와 TTL 관리
- WebSocket 상태 전송
- 대기열 쿠키 처리

다음과 같이 유스케이스별로 분리한다.

```text
reserve/application
├─ ReserveScheduleService
├─ ReserveEnrollmentService
├─ ReserveQueueService
└─ ReserveQueryService
```

현재 `global.redis.RedisQueueService`, `global.socket`, `global.security.normal.AccessControlService`는 예약 업무에 종속되어 있으므로 reserve로 이동한다.

```text
reserve/infrastructure
├─ redis
│  └─ RedisReserveQueue
└─ websocket
   ├─ ReserveSocketConfig
   ├─ ReserveSocketSessions
   └─ ReserveSocketDisconnectHandler
```

Reserve 응답이 전체 Popup 응답을 포함하면 Goods·Review·Image까지 불필요하게 연결될 수 있다. 예약 응답에는 `PopupSummary`처럼 필요한 필드만 가진 요약 DTO를 사용한다.

### 4.6 orders

OrdersService는 현재 구조 개선 우선순위가 가장 높다. 하나의 서비스가 다음 기능을 함께 처리한다.

- 결제 의도 발급 및 검증
- PortOne 결제 조회
- 예약 굿즈 주문 생성
- 재고 굿즈 주문 생성
- 상품·수량·금액 검증
- 재고 및 예약 인원 차감
- 포인트 사용·적립·복원
- 주문 취소와 환불 요청
- 배송·구매 확정 상태 전이
- 고객 및 기업 주문 조회

다음과 같이 분리한다.

```text
orders
├─ api
├─ application
│  ├─ PaymentIntentService
│  ├─ StockOrderService
│  ├─ ReserveOrderService
│  ├─ OrderCancellationService
│  ├─ OrderStatusService
│  └─ OrderQueryService
├─ domain
│  ├─ Orders
│  ├─ OrdersDetail
│  ├─ PaymentIntent
│  └─ OrderStatus
└─ infrastructure
   ├─ persistence
   └─ payment
      ├─ PaymentClient
      └─ PortOnePaymentClient
```

외부 결제 SDK의 `Payment` 객체가 OrdersService와 환불 이벤트 전체에 전파되지 않도록 내부 결제 응답 모델로 변환한다. 결제 의도, 주문 생성과 환불 보상은 명확한 트랜잭션 경계를 가져야 한다.

Orders가 Customer, Goods, Popup, Reserve의 Repository를 모두 직접 사용하는 구조는 유스케이스 조합에는 필요할 수 있지만, 도메인 규칙과 외부 연동을 한 클래스에 집중시켜서는 안 된다.

### 4.7 payout

현재 크기가 작아 즉시 분리할 필요는 낮다. Orders 완료 내역을 조회하여 정산을 생성하는 관계도 업무상 자연스럽다.

다만 PayoutService가 OrdersRepository의 내부 쿼리에 직접 종속되지 않도록 다음 중 하나를 선택한다.

- Orders가 정산 대상 조회 Port를 제공한다.
- 주문 완료 이벤트를 받아 정산 원장을 누적한다.
- 정산 전용 읽기 Repository를 둔다.

다중 서버에서 스케줄러가 동시에 실행될 수 있으므로 정산 생성의 unique 기준과 멱등성을 함께 설계해야 한다.

### 4.8 global

`global`은 기술 공통 기능만 가져야 하지만 현재 예약·계정·도메인 상태가 섞여 있다.

유지 가능한 공통 기능은 다음과 같다.

- 공통 API 응답 형식
- 전역 예외 변환
- JPA BaseEntity
- 요청 값 검증 유틸리티
- 요청 URL 계산 유틸리티

이동해야 할 기능은 다음과 같다.

| 현재 위치 | 권장 위치 |
|---|---|
| `global.redis.RedisQueueService` | `reserve.infrastructure.redis` |
| `global.socket.*` | `reserve.infrastructure.websocket` |
| `global.security.normal.AccessControlService` | `reserve.security` |
| `global.enums.GoodsStatus` | `goods.domain` |
| `global.enums.OrdersStatus` | `orders.domain` |
| `global.enums.PopupStatus` | `popup.domain` |
| `global.enums.PayoutStatus` | `payout.domain` |
| `global.config.PortOneConfig` | `orders.infrastructure.payment` |
| `global.mail.MailService` | `infrastructure.mail` |
| `global.upload.*` | `infrastructure.storage` |

`global.base`에는 persistence, API 응답, 업무 오류, 시스템 오류가 혼재한다. 다음처럼 목적별로 분리한다.

```text
common
├─ api
│  ├─ ApiResponse
│  ├─ ErrorResponse
│  └─ GlobalExceptionHandler
├─ error
│  ├─ ErrorCode
│  ├─ BusinessException
│  └─ SystemException
├─ persistence
│  └─ BaseEntity
└─ web
   ├─ RequestValidation
   └─ RequestUrlService
```

## 5. 의존 방향 원칙

### 5.1 기본 방향

```text
api/controller
      ↓
application/use case
      ↓
domain
      ↑
infrastructure
```

- Controller는 Repository를 직접 사용하지 않는다.
- Entity는 Controller DTO나 외부 SDK 타입을 참조하지 않는다.
- Domain은 Redis, PortOne, S3, SMTP 같은 기술을 알지 않는다.
- Infrastructure는 Domain 또는 Application이 정의한 인터페이스를 구현한다.
- 다른 도메인의 Repository를 직접 사용하는 대신 좁은 조회 Port 또는 상위 Facade를 우선 사용한다.

### 5.2 도메인 간 참조

다른 도메인의 Entity를 그대로 반환하거나 수정하지 않는다. 다음 중 하나를 선택한다.

1. 식별자만 전달한다.
2. 읽기 전용 요약 모델을 전달한다.
3. 필요한 기능을 인터페이스로 노출한다.
4. 여러 도메인을 조합하는 책임은 Application Facade에 둔다.

### 5.3 DTO 규칙

- API 응답 DTO는 해당 API를 소유한 패키지에서 정의한다.
- 다른 기능의 응답 DTO를 그대로 중첩하지 않는다.
- 목록 응답과 상세 응답을 구분한다.
- Entity의 `toDto()`에 복잡한 다른 도메인 조회를 넣지 않는다.
- 외부 결제·메일·스토리지 SDK의 응답 객체를 Controller나 Domain까지 전달하지 않는다.

### 5.4 JPA 관계 규칙

- 실제 탐색과 불변식 유지에 필요한 방향만 매핑한다.
- 조회 편의를 위한 모든 역방향 `@OneToMany`를 만들지 않는다.
- Aggregate 경계를 넘는 cascade와 orphanRemoval은 사용하지 않는다.
- 다른 Aggregate의 변경은 해당 서비스 또는 명시적 유스케이스를 통해 처리한다.

### 5.5 오류 코드 규칙

모든 업무 오류를 하나의 `ServiceErrorCode`에 추가하지 않는다. 공통 인터페이스를 두고 도메인별 enum으로 분리한다.

```java
public interface ErrorCode {
    int getCode();
    String getMessage();
}
```

```text
account/error/AccountErrorCode
cart/error/CartErrorCode
goods/error/GoodsErrorCode
orders/error/OrdersErrorCode
popup/error/PopupErrorCode
reserve/error/ReserveErrorCode
payout/error/PayoutErrorCode
```

HTTP 상태는 오류 유형에 맞게 결정한다. 모든 업무 예외를 일괄적으로 400으로 변환하지 않는다.

## 6. 목표 패키지 구조

단기적으로 적용 가능한 목표 구조는 다음과 같다.

```text
com.fiiiiive.zippop
├─ common
│  ├─ api
│  ├─ error
│  ├─ persistence
│  └─ web
├─ infrastructure
│  ├─ config
│  ├─ mail
│  ├─ security
│  └─ storage
├─ account
│  ├─ api
│  ├─ application
│  ├─ domain
│  └─ infrastructure
├─ cart
│  ├─ api
│  ├─ application
│  ├─ domain
│  └─ infrastructure
├─ goods
├─ orders
├─ payout
├─ popup
└─ reserve
```

각 기능 패키지는 규모가 커질 때만 `api/application/domain/infrastructure` 하위 구조를 도입한다. 파일 수가 적은 기능에 형식적인 빈 계층을 만들 필요는 없다.

## 7. 리팩터링 우선순위

### 1단계: 신규 결합 증가 차단

- 신규 기능을 `global`에 추가하지 않는다.
- 신규 API는 다른 기능의 DTO를 재사용하지 않는다.
- 신규 외부 연동은 인터페이스 뒤에 둔다.
- 신규 양방향 JPA 관계를 추가하기 전에 실제 탐색 필요성을 확인한다.

### 2단계: 예약 인프라 이동

- RedisQueueService를 reserve로 이동한다.
- WebSocket 세션·인터셉터·disconnect 처리를 reserve로 이동한다.
- 예약 접근 제어를 reserve authorization 컴포넌트로 이동한다.

이 단계는 패키지 이동 위주로 진행하고 동작 변경은 최소화한다.

### 3단계: Popup 허브 해체

- CompanyPopupService의 주문·예약·정산 조회를 기업 마이페이지 Facade로 이동한다.
- PopupService의 구매 이력 검사를 조회 Port로 교체한다.
- Popup Entity의 불필요한 역방향 관계를 제거한다.

### 4단계: Orders 분할

- 결제 의도 발급과 검증을 별도 서비스로 분리한다.
- 재고 주문과 예약 주문 유스케이스를 분리한다.
- 취소·환불과 배송·구매 확정 상태 전이를 분리한다.
- PortOne SDK를 PaymentClient 어댑터 뒤로 이동한다.
- 동시성, 멱등성, 환불 보상 테스트를 먼저 확보한다.

### 5단계: DTO 및 오류 코드 분리

- Cart, Reserve, Orders에서 다른 기능 DTO 재사용을 제거한다.
- 도메인별 오류 코드를 분리한다.
- GlobalExceptionHandler는 공통 ErrorCode 인터페이스를 처리하게 한다.

### 6단계: JPA Aggregate 정리

- 사용하지 않는 역방향 컬렉션을 제거한다.
- cascade와 orphanRemoval 범위를 재검토한다.
- 조회 화면은 Entity 그래프 대신 전용 조회 쿼리와 Projection을 사용한다.

## 8. 변경 완료 기준

구조 변경은 파일 이동만으로 완료 처리하지 않는다. 다음 조건을 만족해야 한다.

- 기존 API 계약이 유지되거나 변경 사항이 명시적으로 문서화되어 있다.
- 백엔드 전체 테스트가 통과한다.
- 프런트엔드 단위 테스트와 프로덕션 빌드가 통과한다.
- 애플리케이션이 실제 MariaDB 스키마에서 `ddl-auto=validate`로 기동한다.
- 순환 패키지 의존이 새로 추가되지 않는다.
- Controller가 다른 기능의 Repository를 직접 사용하지 않는다.
- 외부 SDK 타입이 Domain Entity 또는 API 응답에 노출되지 않는다.
- DB 마이그레이션이 필요한 경우 애플리케이션 배포 전에 적용 순서와 롤백 방법이 준비되어 있다.

## 9. 리팩터링 시 주의사항

- 현재 워크트리에는 Account 통합과 여러 버그 수정이 함께 존재하므로 대규모 패키지 이동을 동시에 진행하지 않는다.
- 구조 변경과 업무 동작 변경을 가능하면 별도 작업으로 분리한다.
- Entity 패키지 이동은 JPA Entity name, JPQL, 테스트 import와 직렬화 영향을 함께 확인한다.
- 오류 코드 분리 시 기존 숫자 코드의 외부 사용 여부를 먼저 확인한다.
- Redis 키 이름과 WebSocket destination은 패키지 이동과 무관하게 호환성을 유지한다.
- 결제 관련 구조 변경은 진행 중 결제창과 구버전 프런트 요청의 유입을 고려해 백엔드·프런트·DB 배포 순서를 함께 설계한다.

## 10. 결론

현재 구조의 문제는 기능별 패키지가 없다는 것이 아니라, 기능 패키지 사이의 경계가 실제 코드에서 지켜지지 않는다는 점이다.

`account`와 `goods`는 현재 구조를 다듬는 방식으로 개선할 수 있다. `popup`, `orders`, `reserve`는 책임 분리와 의존 방향 교정이 필요하다. 특히 Popup을 기업 전체 조회의 진입점으로 사용하고 OrdersService에 결제·재고·포인트·환불을 모두 집중시키는 구조부터 우선적으로 해소한다.

최종 목표는 마이크로서비스 전환이 아니라, 하나의 애플리케이션 안에서도 기능별 변경 범위와 트랜잭션 책임을 명확하게 만드는 모듈형 모놀리스다.
