package com.stackdarker.platform.auth.api;

import com.stackdarker.platform.auth.dto.PagedSessionsResponse;
import com.stackdarker.platform.auth.service.SessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionsController {

    private final SessionService sessionService;

    public SessionsController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/v1/sessions")
    public PagedSessionsResponse list(
            @RequestParam(required = false, defaultValue = "20") int limit,
            @RequestParam(required = false) String cursor
    ) {
        return sessionService.list(limit, cursor);
    }
}
