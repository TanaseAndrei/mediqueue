package ro.mediqueue.api.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderScheduler {

    // TODO: inject NotificationJobRepository, EmailSender, SmsSender

    /**
     * Picks up PENDING notification jobs whose send_at is in the past and dispatches them.
     * Runs every minute; ShedLock ensures only one instance runs at a time in a cluster.
     */
    @Scheduled(fixedDelayString = "60000")
    @SchedulerLock(name = "ReminderScheduler_processNotifications",
                   lockAtMostFor = "5m",
                   lockAtLeastFor = "30s")
    public void processNotifications() {
        log.debug("Processing pending notification jobs");
        // TODO: implement
        //   1. find all PENDING jobs where send_at <= now()
        //   2. for each job: route to EmailSender or SmsSender based on type
        //   3. on success: status=SENT, sent_at=now()
        //   4. on failure: increment attempts, status=FAILED if attempts >= 3, else leave PENDING
    }
}
