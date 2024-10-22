package io.github.vcvitaly.solidgate.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BalanceUpdateScheduler {

    private final BalanceUpdateService service;

    @Scheduled(fixedDelay = 5000)
    public void processRequest() {
        service.processRequest();
    }
}
