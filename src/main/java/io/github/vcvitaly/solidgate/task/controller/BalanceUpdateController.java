package io.github.vcvitaly.solidgate.task.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
public class BalanceUpdateController {

    @PutMapping("/set-users-balance")
    @ResponseStatus(HttpStatus.OK)
    public void setUsersBalance() {

    }

    @GetMapping("/in-progress-requests")
    public String getInProgressRequests() {
        return null;
    }

    @GetMapping("/update-request-status/{idempotencyKey}")
    public String getUpdateRequestStatus(@PathVariable("idempotencyKey") String idempotencyKey) {
        return null;
    }
}
