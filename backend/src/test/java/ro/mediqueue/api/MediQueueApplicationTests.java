package ro.mediqueue.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import ro.mediqueue.api.notification.service.ReminderScheduler;

/**
 * Smoke test — verifies the Spring application context loads without errors.
 *
 * Strategy: we use H2 in PostgreSQL compatibility mode so TIMESTAMPTZ and other
 * PostgreSQL types are accepted, and set ddl-auto=none so Hibernate does not attempt
 * to generate DDL (Flyway is disabled; H2 will have an empty schema but context
 * wiring is the only thing under test here).
 *
 * Real integration tests against a live PostgreSQL instance use @Testcontainers.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false",
        "app.jwt.secret=test-secret-for-unit-tests-min-32-chars!!",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class MediQueueApplicationTests {

    // Replace the scheduler bean with a mock so @Scheduled tasks do not fire
    // during context startup — the shedlock table does not exist in the H2 test DB.
    @MockBean
    @SuppressWarnings("unused")
    private ReminderScheduler reminderScheduler;

    @Test
    void contextLoads() {
        // Spring context load is the assertion itself
    }
}
