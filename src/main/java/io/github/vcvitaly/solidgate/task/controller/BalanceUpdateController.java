package io.github.vcvitaly.solidgate.task.controller;

import io.github.vcvitaly.solidgate.task.exception.IdempotencyKeyNotFoundException;
import io.github.vcvitaly.solidgate.task.exception.NegativeBalanceException;
import io.github.vcvitaly.solidgate.task.exception.UsersNotFoundException;
import io.github.vcvitaly.solidgate.task.service.BalanceUpdateService;
import io.github.vcvitaly.solidgate.task.service.BalanceUpdateValidator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/v1/balance-update"))
public class BalanceUpdateController {

    private final BalanceUpdateService service;
    private final BalanceUpdateValidator validator;

    @PostMapping("/{idempotencyKey}/set-users-balance")
    @ResponseStatus(HttpStatus.OK)
    public void setUsersBalance(@PathVariable("idempotencyKey") String idempotencyKey,
                                @RequestBody Map<Integer, Integer> req) {
        validator.validateUpdateRequest(idempotencyKey, req);
    }

    @GetMapping("/in-progress-requests")
    public String getInProgressRequests() {
        return null;
    }

    @GetMapping("/{idempotencyKey}/update-request-status")
    public String getUpdateRequestStatus(@PathVariable("idempotencyKey") String idempotencyKey) {
        validator.validateIdempotencyKeyExists(idempotencyKey);
        return null;
    }

    @ExceptionHandler(IdempotencyKeyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleException(IdempotencyKeyNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler({NegativeBalanceException.class, UsersNotFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Exception e) {
        return e.getMessage();
    }
}
