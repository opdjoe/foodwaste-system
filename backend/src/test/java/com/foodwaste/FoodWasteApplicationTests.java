package com.foodwaste;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Application context smoke test.
 * Verifies the Spring context loads without errors.
 * Uses H2 in-memory DB (see src/test/resources/application.properties).
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Loads")
class FoodWasteApplicationTests {

    @Test
    @DisplayName("Spring context starts up successfully")
    void contextLoads() {
        // If this test passes, all beans wired correctly
    }
}
