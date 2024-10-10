package io.github.vcvitaly.solidgate.task;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1")
public class BalanceUpdateController {

    @PutMapping("/set-users-balance")
    @ResponseStatus(HttpStatus.OK)
    public void setUsersBalance() {

    }

    @GetMapping("/update-request-status")
    public String getBalanceUpdateRequestStatus() {
        return null;
    }
}
