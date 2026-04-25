package ro.mediqueue.api.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class ShedLockConfig {

    /**
     * JdbcTemplate-backed LockProvider for production and local dev.
     * Excluded from the "test" profile because the smoke test uses H2 without
     * the shedlock table (Flyway is disabled in that context).
     */
    @Bean
    @Profile("!test")
    public LockProvider lockProvider(DataSource dataSource) {
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                        .withJdbcTemplate(new JdbcTemplate(dataSource))
                        // Uses TIMESTAMPTZ-compatible time supplier for PostgreSQL
                        .usingDbTime()
                        .build()
        );
    }
}
