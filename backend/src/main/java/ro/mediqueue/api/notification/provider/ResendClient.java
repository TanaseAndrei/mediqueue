package ro.mediqueue.api.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResendClient {

    // TODO: inject RestClient (Spring 6 RestClient or RestTemplate)

    @Value("${app.resend.api-key:not-configured}")
    private String apiKey;

    @Value("${app.resend.from-address:noreply@mediqueue.ro}")
    private String fromAddress;

    /**
     * Sends an email via the Resend HTTP API (https://resend.com/docs/api-reference/emails/send-email).
     * Base URL: https://api.resend.com/emails
     */
    public void send(String to, String subject, String htmlBody) {
        // TODO: build JSON payload { from, to, subject, html }
        // TODO: POST to https://api.resend.com/emails with Authorization: Bearer {apiKey}
        // TODO: throw on non-2xx response
        log.info("[STUB] Would send email to {} with subject '{}'", to, subject);
    }
}
