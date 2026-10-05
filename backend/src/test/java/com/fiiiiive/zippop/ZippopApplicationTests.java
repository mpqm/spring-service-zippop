package com.fiiiiive.zippop;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.main.lazy-initialization=true",
		"server.port=0",
		"server.addr=127.0.0.1",
		"spring.datasource.url=jdbc:h2:mem:zippop;MODE=MariaDB;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.security.jwt.secret=01234567890123456789012345678901",
		"redis.host=127.0.0.1",
		"redis.port=6379",
		"redis.password=test",
		"upload.type=local",
		"upload.local.path=build/test-uploads",
		"upload.s3.bucket=test",
		"upload.s3.credentials.access-key=test",
		"upload.s3.credentials.secret-key=test",
		"upload.s3.region.static=ap-northeast-2",
		"imp.imp_key=test",
		"imp.imp_secret=test",
		"spring.mail.host=localhost",
		"spring.mail.port=2525",
		"spring.mail.username=test",
		"spring.mail.password=test"
})
class ZippopApplicationTests {

	@MockBean
	RedissonClient redissonClient;

	@Test
	void contextLoads() {
	}

}
