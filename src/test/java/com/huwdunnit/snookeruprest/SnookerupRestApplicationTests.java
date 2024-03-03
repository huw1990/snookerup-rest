package com.huwdunnit.snookeruprest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
class SnookerupRestApplicationTests extends BaseIT {

	@Test
	void contextLoads() {
	}

}
