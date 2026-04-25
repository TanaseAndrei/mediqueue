package ro.mediqueue.api.notification.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsRoClient {

    @Value("${app.smsro.api-key:not-configured}")
    private String apiKey;

    /**
     * Sends an SMS via the SMS.ro API (https://sms.ro/api/).
     */
    public void send(String to, String message) {
        // TODO: POST to https://api.sms.ro/sms/send with apiKey, to, message parameters
        // TODO: throw on error response
        log.info("[STUB] Would send SMS to {} with message '{}'", to, message);
    }
}
