import pymysql
from faker import Faker
import requests
from datetime import datetime, timedelta
import random

# MariaDB 연결 정보 설정
db_config = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "qwer1234",
    "database": "zippop",
}

# Faker 초기화
faker = Faker('ko_KR')

# MariaDB 연결
connection = pymysql.connect(**db_config)
cursor = connection.cursor()

# 테이블 이름 설정
store_table_name = "store"
store_image_table_name = "store_image"
store_review_table_name = "store_review"
goods_table_name = "goods"
goods_image_table_name = "goods_image"
company_table_name = "company"
customer_table_name = "customer"
# 배치 사이즈 설정
batch_size = 100

# 데이터 생성 및 삽입 함수 (배치 삽입 => 부하때문에)
def store_data(num_rows):
    # 외래 키 검사를 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    
    # 인덱스 비활성화
    cursor.execute(f"ALTER TABLE {store_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 10))
            address = faker.address()
            category = random.choice(["카테고리1", "카테고리2", "카테고리3", "카테고리4", "카테고리5", "카테고리6", "카테고리7", "카테고리8", "카테고리9", "카테고리10"])
            fixRandomInt = random.randint(1, 100)
            company_email = f"company{fixRandomInt}@company.com"
            content = faker.text(max_nb_chars=200)
            end_date = faker.date_between(start_date="today", end_date="+30d")
            like_count = 0
            name = f"팝업스토어 {i}"  # 이름을 순차적으로 설정
            start_date = faker.date_between(start_date="-30d", end_date="today")
            status = random.choice(["STORE_START", "STORE_END"])
            total_people = random.randint(1, 200)
            company_idx = fixRandomInt

            insert_values.append((
                status,
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                address, category, company_email, content,
                end_date.strftime('%Y-%m-%d'), like_count,
                name, start_date.strftime('%Y-%m-%d'),
                total_people, company_idx
            ))

            # 100개씩 모았을 때 한번에 삽입
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {store_table_name} (
                        status, created_at, updated_at, address, category, company_email, content,
                        end_date, like_count, name, start_date, total_people, company_idx
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
                """, insert_values)
                  # 1000개 삽입 후 출력
                insert_values = []  # 한 번에 삽입 후 초기화
                print(f"------------------------- store_data 100 inserted successfully.")
        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                INSERT INTO {store_table_name} (
                    status, created_at, updated_at, address, category, company_email, content,
                    end_date, like_count, name, start_date, total_people, company_idx
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
            """, insert_values)


        # 한 번만 커밋
        connection.commit()
        print(f"------------------------- store_data total inserted successfully.")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        connection.rollback()

    finally:
        # 인덱스 다시 활성화
        cursor.execute(f"ALTER TABLE {store_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def store_image_data(num_rows):
    # 외래 키 검사를 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    
    # 인덱스 비활성화
    cursor.execute(f"ALTER TABLE {store_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 10))
            url = f"https://picsum.photos/id/{random.randint(1, 300)}/{random.randint(1, 300)}/{random.randint(1, 300)}"
            store_idx = i 

            insert_values.append(( 
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                url,
                store_idx
            ))

            # 100개씩 모았을 때 한번에 삽입
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {store_image_table_name} (
                        created_at, updated_at, url, store_idx
                    ) VALUES (%s, %s, %s, %s);
                """, insert_values)
                  # 1000개 삽입 후 출력
                insert_values = []  # 한 번에 삽입 후 초기화
                print(f"------------------------- store_image_data 100 inserted successfully.")

        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                    INSERT INTO {store_image_table_name} (
                        created_at, updated_at, url, store_idx
                    ) VALUES (%s, %s, %s, %s);
            """, insert_values)

        # 한 번만 커밋
        connection.commit()
        print(f"------------------------- store_image_data total inserted successfully.")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        connection.rollback()

    finally:
        # 인덱스 다시 활성화
        cursor.execute(f"ALTER TABLE {store_image_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def store_review_data(num_rows):
    # 외래 키 검사를 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    
    # 인덱스 비활성화
    cursor.execute(f"ALTER TABLE {store_review_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            fixRandomInt = random.randint(1, 10000)

            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 10))
            content = faker.text(max_nb_chars=200)
            customer_email = f"customer{fixRandomInt}@customer.com"
            customer_name = f"customer{fixRandomInt}"
            customer_idx = fixRandomInt
            store_idx = random.randint(1, 10000)
            rating = random.randint(1, 5)
            title = faker.sentence(nb_words=6)

            insert_values.append((
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                content,
                customer_email,
                customer_name,
                rating,
                title,
                customer_idx,
                store_idx
            ))

            # 100개씩 모았을 때 한번에 삽입
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {store_review_table_name} (
                        created_at, updated_at, content, customer_email, 
                        customer_name, rating, title, customer_idx, store_idx
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s);
                """, insert_values)
                  # 1000개 삽입 후 출력
                insert_values = []  # 한 번에 삽입 후 초기화
                print(f"------------------------- store_review_data 100 inserted successfully.")

        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                INSERT INTO {store_review_table_name} (
                    created_at, updated_at, content, customer_email, 
                    customer_name, rating, title, customer_idx, store_idx
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s);
            """, insert_values)

        # 한 번만 커밋
        connection.commit()
        print(f"------------------------- store_review_data total inserted successfully.")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        connection.rollback()

    finally:
        # 인덱스 다시 활성화
        cursor.execute(f"ALTER TABLE {store_review_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def goods_data(num_rows):
    # 외래 키 검사를 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    
    # 인덱스 비활성화
    cursor.execute(f"ALTER TABLE {goods_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 10))
            amount = random.randint(1, 100)
            content = faker.text(max_nb_chars=200)
            name = faker.word().capitalize()
            price = round(random.uniform(1000, 50000), 2)
            status = random.choice(["GOODS_RESERVED", "GOODS_STOCK"])
            store_idx = random.randint(1, 10000)

            insert_values.append((
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                amount,
                content,
                name,
                price,
                status,
                store_idx
            ))

            # 100개씩 모았을 때 한번에 삽입
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {goods_table_name} (
                        created_at, updated_at, amount, content, 
                        name, price, status, store_idx
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s);
                """, insert_values)
                  # 1000개 삽입 후 출력
                insert_values = []  # 한 번에 삽입 후 초기화
                print(f"------------------------- goods_data 100 inserted successfully.")

        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                INSERT INTO {goods_table_name} (
                    created_at, updated_at, amount, content, 
                    name, price, status, store_idx
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s);
            """, insert_values)

        # 한 번만 커밋
        connection.commit()
        print(f"------------------------- goods_data total inserted successfully.")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        connection.rollback()

    finally:
        # 인덱스 다시 활성화
        cursor.execute(f"ALTER TABLE {goods_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def goods_image_data(num_rows):
    # 외래 키 검사를 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    
    # 인덱스 비활성화
    cursor.execute(f"ALTER TABLE {goods_image_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 10))
            url = f"https://picsum.photos/id/{random.randint(1, 300)}/{random.randint(1, 300)}/{random.randint(1, 300)}"
            goods_idx = i 

            insert_values.append(( 
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                url,
                goods_idx
            ))

            # 100개씩 모았을 때 한번에 삽입
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {goods_image_table_name} (
                        created_at, updated_at, url, goods_idx
                    ) VALUES (%s, %s, %s, %s);
                """, insert_values)
                  # 1000개 삽입 후 출력
                insert_values = []  # 한 번에 삽입 후 초기화
                print(f"------------------------- goods_image_data 100 inserted successfully.")
               

        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                    INSERT INTO {goods_image_table_name} (
                    created_at, updated_at, url, goods_idx
                    ) VALUES (%s, %s, %s, %s);
            """, insert_values)

        # 한 번만 커밋
        connection.commit()
        print(f"------------------------- goods_image_data total inserted successfully.")

    except Exception as e:
        print(f"An unexpected error occurred: {e}")
        connection.rollback()

    finally:
        # 인덱스 다시 활성화
        cursor.execute(f"ALTER TABLE {goods_image_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def company_data(num_rows):
        # 외래 키 및 인덱스 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    cursor.execute(f"ALTER TABLE {company_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 30))
            address = faker.address().replace("\n", ", ")  # 주소
            crn = faker.bothify(text="???-##-#####")  # 임의의 사업자 등록번호
            email = f"company{i}@company.com"  # 이메일
            is_email_auth = 1  # 이메일 인증 여부
            is_in_active = 0  # 비활성화 여부
            name = f"company{i}"  # 사용자 이름
            password = "$2a$10$..wSUVQHJJpCTUY92pyId.H0fUicaJBvaL/F.ilESb.LwMRro.PLy"  # 임의의 비밀번호
            phone_number = faker.phone_number()  # 전화번호
            profile_image_url = f"https://picsum.photos/id/{random.randint(1, 300)}/{random.randint(1, 300)}/{random.randint(1, 300)}"  # 프로필 이미지 URL
            role = "ROLE_COMPANY"
            user_id = f"company{i}"  # 사용자 ID
            
            insert_values.append((
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                address,
                crn,
                email,
                is_email_auth,
                is_in_active,
                name,
                password,
                phone_number,
                profile_image_url,
                role,
                user_id
            ))
            
            # 배치 처리
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {company_table_name} (
                        created_at, updated_at, address, crn, email, is_email_auth,
                        is_in_active, name, password, phone_number, profile_image_url, role, user_id
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
                """, insert_values)
                insert_values = []
                print(f"------------------------- company_data 1000 inserted successfully.")
        
        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                INSERT INTO {company_table_name} (
                    created_at, updated_at, address, crn, email, is_email_auth,
                    is_in_active, name, password, phone_number, profile_image_url, role, user_id
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
            """, insert_values)
            
        
        # 커밋
        connection.commit()
        print(f"------------------------- company_data total inserted successfully.")
    
    except Exception as e:
        print(f"An error occurred: {e}")
        connection.rollback()
    
    finally:
        # 외래 키 및 인덱스 활성화
        cursor.execute(f"ALTER TABLE {company_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

def customer_data(num_rows):
    # 외래 키 및 인덱스 비활성화
    cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")
    cursor.execute(f"ALTER TABLE {customer_table_name} DISABLE KEYS;")
    
    try:
        insert_values = []
        
        for i in range(1, num_rows + 1):
            created_at = faker.date_time_this_year()
            updated_at = created_at + timedelta(days=random.randint(0, 30))
            address = faker.address().replace("\n", ", ")  # 주소
            email = f"customer{i}@customer.com"  # 이메일
            is_email_auth = 1  # 이메일 인증 여부
            is_in_active = 0  # 비활성화 여부
            name = f"customer{i}"  # 사용자 이름
            password = "$2a$10$..wSUVQHJJpCTUY92pyId.H0fUicaJBvaL/F.ilESb.LwMRro.PLy"  # 임의의 비밀번호
            phone_number = faker.phone_number()  # 전화번호
            profile_image_url = f"https://picsum.photos/id/{random.randint(1, 300)}/{random.randint(1, 300)}/{random.randint(1, 300)}"
            role = "ROLE_CUSTOMER"  # 역할
            point = 3000  # 포인트 값은 반드시 숫자여야 합니다.
            user_id = f"customer{i}"  # 사용자 ID
            
            # 삽입할 값 구성
            insert_values.append((
                created_at.strftime('%Y-%m-%d %H:%M:%S'),
                updated_at.strftime('%Y-%m-%d %H:%M:%S'),
                address,
                email,
                is_email_auth,
                is_in_active,
                name,
                password,
                phone_number,
                point,  # 반드시 숫자
                profile_image_url,
                role,
                user_id
            ))
            
            # 배치 처리
            if len(insert_values) >= batch_size:
                cursor.executemany(f"""
                    INSERT INTO {customer_table_name} (
                        created_at, updated_at, address, email, is_email_auth,
                        is_in_active, name, password, phone_number, point, 
                        profile_image_url, role, user_id
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
                """, insert_values)
                insert_values = []
                print(f"------------------------- customer_data 1000 inserted successfully.")

        # 남은 데이터 삽입
        if insert_values:
            cursor.executemany(f"""
                INSERT INTO {customer_table_name} (
                    created_at, updated_at, address, email, is_email_auth,
                    is_in_active, name, password, phone_number, point, 
                    profile_image_url, role, user_id
                ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);
            """, insert_values)
            print(f"Inserted remaining {len(insert_values)} rows.")
        
        # 커밋
        connection.commit()
        print(f"------------------------- customer_data total inserted successfully.")
    except Exception as e:
        print(f"An error occurred: {e}")
        connection.rollback()
    
    finally:
        # 외래 키 및 인덱스 활성화
        cursor.execute(f"ALTER TABLE {customer_table_name} ENABLE KEYS;")
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

try:
    store_data(10000)
    store_image_data(10000)
    store_review_data(10000)
    goods_data(50000)
    goods_image_data(50000)
    company_data(100)
    customer_data(10000)

except pymysql.MySQLError as e:
    print(f"MySQL Error: {e}")
except Exception as e:
    print(f"Error: {e}")
finally:
    cursor.close()
    connection.close()
