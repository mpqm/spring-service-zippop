"""ZIPPOP 포트폴리오용 시나리오 데이터 시더.

사용법:
    python meta/script/data-init.py --reset

`--reset`은 로컬 포트폴리오 DB의 기존 데이터를 비운 뒤 다시 채운다.
DB 접속 정보는 환경 변수 또는 `cicd/.env`에서 읽는다.
"""

from __future__ import annotations

import argparse
import os
import uuid
from datetime import date, datetime, time, timedelta
from pathlib import Path

import pymysql


ROOT = Path(__file__).resolve().parents[2]
NOW = datetime.now().replace(microsecond=0)
TODAY = date.today()
PASSWORD_HASH = "$2a$10$..wSUVQHJJpCTUY92pyId.H0fUicaJBvaL/F.ilESb.LwMRro.PLy"


def load_env_file() -> dict[str, str]:
    values: dict[str, str] = {}
    env_path = ROOT / "cicd" / ".env"
    if not env_path.exists():
        return values
    for raw_line in env_path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key.strip()] = value.strip().strip('"').strip("'")
    return values


def db_connection():
    file_env = load_env_file()

    def value(name: str, default: str = "") -> str:
        return os.getenv(name, file_env.get(name, default))

    return pymysql.connect(
        host=os.getenv("ZIPPOP_DB_HOST", "127.0.0.1"),
        port=int(os.getenv("ZIPPOP_DB_PORT", "3306")),
        user=os.getenv("ZIPPOP_DB_USER", "root"),
        password=value("MARIADB_ROOT_PASSWORD"),
        database=value("MARIADB_DATABASE", "zippop"),
        charset="utf8mb4",
        autocommit=False,
    )


COMPANIES = [
    ("무신사 테라스", "demo.musinsa@zippop.kr", "demo_musinsa", "110-81-90001", "서울 성동구 아차산로 13"),
    ("성수 큐레이션", "demo.seongsu@zippop.kr", "demo_seongsu", "110-81-90002", "서울 성동구 연무장길 20"),
    ("컬처 웍스", "demo.culture@zippop.kr", "demo_culture", "110-81-90003", "서울 마포구 양화로 45"),
]

CUSTOMERS = [
    ("김지우", "demo.jiwoo@zippop.kr", "demo_jiwoo", 12500),
    ("박서준", "demo.seojun@zippop.kr", "demo_seojun", 7800),
    ("이하늘", "demo.haneul@zippop.kr", "demo_haneul", 4200),
    ("최민아", "demo.mina@zippop.kr", "demo_mina", 15300),
]

# name, category, address, description, start offset, end offset, capacity, company index, image
ACTIVE_POPUPS = [
    ("잔망루피의 퇴근 연구소", "캐릭터", "서울 성동구 성수이로 18길 20", "퇴근 후의 작은 행복을 연구하는 잔망루피 체험형 팝업. 한정 데스크 굿즈를 예약 구매할 수 있어요.", -4, 18, 120, 0, "photo-1561214115-f2f134cc4912"),
    ("NEWJEANS BUNNIES CLUB", "뮤직", "서울 영등포구 여의대로 108", "음악과 미디어 아트로 만나는 버니즈 클럽. 회차 예약으로 기다림 없이 입장하세요.", -2, 12, 80, 2, "photo-1524368535928-5b5e00ddc76b"),
    ("오롤리데이 HAPPY CAMP", "라이프스타일", "서울 성동구 연무장길 33", "기분 좋은 일상을 위한 문구와 리빙 제품을 캠프 콘셉트로 선보입니다.", -7, 9, 100, 1, "photo-1523726491678-bf852e717f6a"),
    ("TAMBURINS PERFUME ARCHIVE", "뷰티", "서울 강남구 압구정로46길 50", "향의 기억을 공간으로 풀어낸 퍼퓸 아카이브. 현장 체험과 한정 키트를 만나보세요.", -10, 15, 60, 0, "photo-1547887538-e3a2f32cb1cc"),
    ("SEOUL DESSERT LAB", "푸드", "서울 용산구 한강대로 23길 55", "서울의 인기 디저트 브랜드 여섯 곳이 함께 만드는 한정 메뉴와 굿즈 마켓입니다.", -1, 20, 140, 1, "photo-1551024506-0bccd828d307"),
    ("MARVEL HERO TRAINING", "게임·콘텐츠", "서울 송파구 올림픽로 300", "히어로 미션을 수행하고 한정 배지를 모으는 인터랙티브 체험형 팝업입니다.", -3, 14, 160, 2, "photo-1608889175123-8ee362201f81"),
    ("MONOCLE SEOUL WEEKEND", "패션", "서울 용산구 이태원로 240", "도시 여행자를 위한 패션, 서적, 로컬 브랜드를 한 공간에서 소개합니다.", -5, 8, 70, 0, "photo-1441986300917-64674bd600d8"),
    ("GREEN RECORD PLANT SHOP", "라이프스타일", "서울 마포구 동교로 29길 34", "음악을 들으며 반려 식물을 고르는 주말 플랜트 숍. 리사이클 화분도 준비했어요.", -2, 24, 90, 1, "photo-1416879595882-3373a0480b5b"),
]

ENDED_POPUPS = [
    ("PEANUTS 75th ANNIVERSARY", "캐릭터", "서울 서대문구 연희로 11가길 48", "전시 종료 후에도 스누피 한정 굿즈의 남은 재고를 ZIPPOP에서 만나보세요.", -55, -20, 110, 0, "photo-1575361204480-aadea25e6e68"),
    ("NATIONAL GEOGRAPHIC OCEAN", "전시", "서울 강남구 영동대로 513", "바다 보전 전시의 아트 포스터와 업사이클 굿즈 잔여 수량을 온라인 판매합니다.", -48, -12, 180, 2, "photo-1484291470158-b8f8d608850d"),
    ("BEAN BROTHERS ROASTING WEEK", "푸드", "서울 마포구 토정로 35길 17", "행사는 끝났지만 팝업 전용 블렌드와 드립백 세트를 재고 소진 시까지 판매합니다.", -36, -8, 90, 1, "photo-1447933601403-0c6688de566e"),
    ("OBJECT SMALL THINGS FAIR", "디자인", "서울 종로구 자하문로 10길 4", "독립 작가들의 작은 물건을 소개한 전시의 마지막 재고를 합리적인 가격으로 연결합니다.", -60, -25, 70, 1, "photo-1494438639946-1ebd1d20bf85"),
]

ACTIVE_GOODS = [
    ("사전예약 한정 키링", "현장 입장 후 수령하는 사전예약 전용 아크릴 키링", 12000),
    ("팝업 익스클루시브 티셔츠", "팝업 로고를 담은 오버핏 코튼 티셔츠", 39000),
]

STOCK_GOODS = [
    ("온라인 라스트 에디션 토트백", "팝업 종료 후 남은 수량만 판매하는 코튼 토트백", 24000),
    ("아카이브 포스터 세트", "팝업의 키 비주얼을 담은 A3 포스터 3종", 18000),
    ("랜덤 스티커 팩", "한정 그래픽 스티커 8매 구성", 8000),
]

IMAGE_BASE = "https://images.unsplash.com/"
IMAGE_QUERY = "?auto=format&fit=crop&w=1200&q=85"
POPUPS_PER_STATUS = 120


def expand_popups(templates, count: int):
    """Create enough distinct popup rows to exercise list pagination."""
    for index in range(count):
        template = templates[index % len(templates)]
        cycle = index // len(templates) + 1
        name, category, address, content, start_offset, end_offset, capacity, company_index, image_id = template
        yield (
            f"{name} #{index + 1:03d}",
            category,
            f"{address} ({cycle})",
            content,
            start_offset,
            end_offset,
            capacity + (index % 5) * 10,
            company_index,
            image_id,
        )


def reset_database(cursor) -> None:
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0")
    for table in [
        "payout", "orders_detail", "orders", "cart_item", "cart", "popup_like",
        "popup_review", "reserve", "goods_image", "goods", "popup_image",
        "popup", "customer", "company",
    ]:
        cursor.execute(f"TRUNCATE TABLE `{table}`")
    cursor.execute("SET FOREIGN_KEY_CHECKS = 1")


def insert_account_data(cursor) -> tuple[list[int], list[int]]:
    company_ids = []
    for index, (name, email, user_id, crn, address) in enumerate(COMPANIES, start=1):
        cursor.execute(
            """INSERT INTO company
               (created_at, updated_at, address, crn, email, is_email_auth, is_in_active,
                name, password, phone_number, profile_image_url, role, user_id)
               VALUES (%s,%s,%s,%s,%s,1,0,%s,%s,%s,%s,'ROLE_COMPANY',%s)""",
            (NOW, NOW, address, crn, email, name, PASSWORD_HASH, f"0109000000{index}", None, user_id),
        )
        company_ids.append(cursor.lastrowid)

    customer_ids = []
    for index, (name, email, user_id, point) in enumerate(CUSTOMERS, start=1):
        cursor.execute(
            """INSERT INTO customer
               (created_at, updated_at, address, email, is_email_auth, is_in_active, name,
                password, phone_number, point, profile_image_url, role, user_id)
               VALUES (%s,%s,%s,%s,1,0,%s,%s,%s,%s,%s,'ROLE_CUSTOMER',%s)""",
            (NOW, NOW, "서울특별시 성동구 성수동", email, name, PASSWORD_HASH,
             f"0108000000{index}", point, None, user_id),
        )
        customer_ids.append(cursor.lastrowid)
    return company_ids, customer_ids


def insert_popup(cursor, data, company_ids, status: str) -> int:
    name, category, address, content, start_offset, end_offset, capacity, company_index, image_id = data
    start_date = TODAY + timedelta(days=start_offset)
    end_date = TODAY + timedelta(days=end_offset)
    likes = 18 + (abs(start_offset) * 7) % 93
    cursor.execute(
        """INSERT INTO popup
           (created_at, updated_at, address, category, company_email, content, end_date,
            like_count, name, start_date, status, total_people, company_idx)
           VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)""",
        (NOW, NOW, address, category, COMPANIES[company_index][1], content, end_date,
         likes, name, start_date, status, capacity, company_ids[company_index]),
    )
    popup_id = cursor.lastrowid
    cursor.execute(
        "INSERT INTO popup_image (created_at, updated_at, url, popup_idx) VALUES (%s,%s,%s,%s)",
        (NOW, NOW, f"{IMAGE_BASE}{image_id}{IMAGE_QUERY}", popup_id),
    )
    return popup_id


def insert_goods(cursor, popup_id: int, goods, status: str, image_seed: int) -> None:
    for offset, (name, content, price) in enumerate(goods):
        cursor.execute(
            """INSERT INTO goods
               (created_at, updated_at, amount, content, name, price, status, popup_idx)
               VALUES (%s,%s,%s,%s,%s,%s,%s,%s)""",
            (NOW, NOW, 8 + ((image_seed + offset) * 7) % 35, content, name, price, status, popup_id),
        )
        goods_id = cursor.lastrowid
        image_ids = [
            "photo-1523275335684-37898b6baf30",
            "photo-1521572163474-6864f9cf17ab",
            "photo-1499951360447-b19be8fe80f5",
            "photo-1542291026-7eec264c27ff",
        ]
        cursor.execute(
            "INSERT INTO goods_image (created_at, updated_at, url, goods_idx) VALUES (%s,%s,%s,%s)",
            (NOW, NOW, f"{IMAGE_BASE}{image_ids[(image_seed + offset) % len(image_ids)]}{IMAGE_QUERY}", goods_id),
        )


def seed(cursor) -> dict[str, int]:
    company_ids, customer_ids = insert_account_data(cursor)
    active_ids = []
    ended_ids = []

    for index, popup in enumerate(expand_popups(ACTIVE_POPUPS, POPUPS_PER_STATUS)):
        popup_id = insert_popup(cursor, popup, company_ids, "POPUP_START")
        active_ids.append(popup_id)
        insert_goods(cursor, popup_id, ACTIVE_GOODS, "GOODS_RESERVED", index)
        for slot in range(3):
            slot_date = TODAY + timedelta(days=slot + 1 + index % 3)
            start_at = datetime.combine(slot_date, time(11 + slot * 2, 0))
            cursor.execute(
                """INSERT INTO reserve
                   (created_at, updated_at, end_time, start_date, start_time, total_people,
                    waitinguuid, workinguuid, popup_idx)
                   VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)""",
                (NOW, NOW, start_at + timedelta(hours=1, minutes=30), slot_date, start_at,
                 20 + slot * 10, str(uuid.uuid4()), str(uuid.uuid4()), popup_id),
            )

    for index, popup in enumerate(
        expand_popups(ENDED_POPUPS, POPUPS_PER_STATUS),
        start=POPUPS_PER_STATUS,
    ):
        popup_id = insert_popup(cursor, popup, company_ids, "POPUP_END")
        ended_ids.append(popup_id)
        insert_goods(cursor, popup_id, STOCK_GOODS, "GOODS_STOCK", index)

    reviews = [
        ("예약 덕분에 편했어요", "줄을 오래 서지 않고 바로 입장해서 팝업을 충분히 즐겼어요.", 5),
        ("굿즈 선구매가 유용해요", "품절 걱정 없이 현장에서 받을 수 있어서 좋았습니다.", 5),
        ("종료 후에도 구매 가능", "현장에서 놓친 굿즈를 재고 마켓에서 찾아서 만족해요.", 4),
        ("다음 팝업도 기대돼요", "일정과 예약 정보를 한곳에서 확인할 수 있어 편리합니다.", 5),
    ]
    for index, (title, content, rating) in enumerate(reviews):
        customer_index = index % len(customer_ids)
        popup_id = (active_ids + ended_ids)[index]
        cursor.execute(
            """INSERT INTO popup_review
               (created_at, updated_at, content, customer_email, customer_name, rating,
                title, customer_idx, popup_idx)
               VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)""",
            (NOW, NOW, content, CUSTOMERS[customer_index][1], CUSTOMERS[customer_index][0],
             rating, title, customer_ids[customer_index], popup_id),
        )

    for customer_id in customer_ids:
        for popup_id in active_ids[:3]:
            cursor.execute(
                "INSERT INTO popup_like (created_at, updated_at, customer_idx, popup_idx) VALUES (%s,%s,%s,%s)",
                (NOW, NOW, customer_id, popup_id),
            )

    return {
        "companies": len(company_ids),
        "customers": len(customer_ids),
        "active_popups": len(active_ids),
        "stock_popups": len(ended_ids),
        "goods": len(active_ids) * len(ACTIVE_GOODS) + len(ended_ids) * len(STOCK_GOODS),
        "reserves": len(active_ids) * 3,
        "reviews": len(reviews),
        "likes": len(customer_ids) * 3,
    }


def main() -> None:
    parser = argparse.ArgumentParser(description="ZIPPOP 포트폴리오 데이터 생성")
    parser.add_argument(
        "--reset",
        action="store_true",
        help="기존 로컬 데이터를 모두 삭제한 뒤 시드 데이터를 생성합니다.",
    )
    args = parser.parse_args()
    if not args.reset:
        parser.error("포트폴리오 DB 초기화를 확인하려면 --reset 옵션이 필요합니다.")

    connection = db_connection()
    try:
        with connection.cursor() as cursor:
            reset_database(cursor)
            counts = seed(cursor)
        connection.commit()
    except Exception:
        connection.rollback()
        raise
    finally:
        connection.close()

    print("ZIPPOP portfolio seed completed")
    for name, count in counts.items():
        print(f"  {name}: {count}")


if __name__ == "__main__":
    main()
