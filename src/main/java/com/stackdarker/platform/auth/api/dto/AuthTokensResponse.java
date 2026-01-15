package com.stackdarker.platform.auth.api.dto;

public class AuthTokensResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private long expiresIn; 

    public AuthTokensResponse() {}

    public AuthTokensResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getTokenType() { return tokenType; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
