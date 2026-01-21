package com.stackdarker.platform.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("platform_auth")
            .withUsername("auth_user")
            .withPassword("auth_pass");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        // Flyway must run against Postgres (uuid-ossp etc)
        r.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void authFlow_register_login_me_sessions_refresh_logout() throws Exception {
        String email = "kai+" + System.currentTimeMillis() + "@example.com";
        String password = "Password123!";

        // 1) Register
        var regRes = mvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Tokens regTokens = Tokens.fromJson(om, regRes);
        assertThat(regTokens.accessToken()).isNotBlank();
        assertThat(regTokens.refreshToken()).isNotBlank();

        // 2) Login
        var loginRes = mvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Tokens loginTokens = Tokens.fromJson(om, loginRes);

        // 3) /me with valid access token
        mvc.perform(get("/v1/auth/me")
                        .header("Authorization", "Bearer " + loginTokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // 4) /sessions with valid access token
        mvc.perform(get("/v1/sessions")
                        .header("Authorization", "Bearer " + loginTokens.accessToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        // 5) Refresh with refresh token
        var refreshRes = mvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(loginTokens.refreshToken())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Tokens refreshed = Tokens.fromJson(om, refreshRes);

        // 6) Logout with current access token + refresh token
        mvc.perform(post("/v1/auth/logout")
                        .header("Authorization", "Bearer " + refreshed.accessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshed.refreshToken())))
                .andExpect(status().isNoContent());

        // 7) Refresh should fail after logout (revoked refresh token)
        mvc.perform(post("/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"refreshToken":"%s"}
                                """.formatted(refreshed.refreshToken())))
                .andExpect(status().isUnauthorized());
    }

    record Tokens(String accessToken, String refreshToken) {
        static Tokens fromJson(ObjectMapper om, String json) throws Exception {
            JsonNode root = om.readTree(json);
            return new Tokens(
                    root.path("accessToken").asText(),
                    root.path("refreshToken").asText()
            );
        }
    }
}
