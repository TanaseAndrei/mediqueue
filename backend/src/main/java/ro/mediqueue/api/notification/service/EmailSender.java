package ro.mediqueue.api.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ro.mediqueue.api.notification.domain.NotificationJob;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSender {

    // TODO: inject ResendClient

    /**
     * Sends an email for the given notification job via the Resend HTTP API.
     * Payload JSON contains: subject, recipientName, bodyHtml template variables.
     */
    public void send(NotificationJob job) {
        // TODO: deserialise job.getPayload() to a typed DTO
        // TODO: call ResendClient.send(...)
        // TODO: log result
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
