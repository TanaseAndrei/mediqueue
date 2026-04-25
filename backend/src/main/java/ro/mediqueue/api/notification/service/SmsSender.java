package ro.mediqueue.api.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.mediqueue.api.notification.domain.NotificationJob;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSender {

    // TODO: inject SmsRoClient

    /**
     * Sends an SMS for the given notification job via the SMS.ro HTTP API.
     */
    public void send(NotificationJob job) {
        // TODO: deserialise job.getPayload() to a typed DTO
        // TODO: call SmsRoClient.send(...)
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
