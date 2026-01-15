package com.stackdarker.platform.auth.service;

import org.springframework.stereotype.Service;

import com.stackdarker.platform.auth.api.dto.*;

@Service
public class AuthService {

    public UserResponse register(RegisterRequest req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AuthTokens login(LoginRequest req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AuthTokens refresh(RefreshRequest req) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void logout() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public UserResponse me() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
