package com.stackdarker.platform.auth.service;

import org.springframework.stereotype.Service;

import com.stackdarker.platform.auth.api.dto.PagedSessionsResponse;

@Service
public class SessionService {

    public PagedSessionsResponse list(int limit, String cursor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
