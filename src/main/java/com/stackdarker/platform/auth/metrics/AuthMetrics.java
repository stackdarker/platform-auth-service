package com.stackdarker.platform.auth.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class AuthMetrics {

    private final Counter registerSuccess;
    private final Counter registerFailure;

    private final Counter loginSuccess;
    private final Counter loginFailure;

    private final Counter refreshSuccess;
    private final Counter refreshFailure;

    private final Timer tokenIssueTimer;

    public AuthMetrics(MeterRegistry registry) {
        this.registerSuccess = Counter.builder("auth_register_total")
                .description("Total successful user registrations")
                .tag("result", "success")
                .register(registry);

        this.registerFailure = Counter.builder("auth_register_total")
                .description("Total failed user registrations")
                .tag("result", "failure")
                .register(registry);

        this.loginSuccess = Counter.builder("auth_login_total")
                .description("Total successful logins")
                .tag("result", "success")
                .register(registry);

        this.loginFailure = Counter.builder("auth_login_total")
                .description("Total failed logins")
                .tag("result", "failure")
                .register(registry);

        this.refreshSuccess = Counter.builder("auth_refresh_total")
                .description("Total successful token refreshes")
                .tag("result", "success")
                .register(registry);

        this.refreshFailure = Counter.builder("auth_refresh_total")
                .description("Total failed token refreshes")
                .tag("result", "failure")
                .register(registry);

        this.tokenIssueTimer = Timer.builder("auth_token_issue_seconds")
                .description("Time to issue tokens (access+refresh)")
                .publishPercentileHistogram()
                .minimumExpectedValue(Duration.ofMillis(1))
                .maximumExpectedValue(Duration.ofSeconds(2))
                .register(registry);
    }

    public void registerSuccess() { registerSuccess.increment(); }
    public void registerFailure() { registerFailure.increment(); }

    public void loginSuccess() { loginSuccess.increment(); }
    public void loginFailure() { loginFailure.increment(); }

    public void refreshSuccess() { refreshSuccess.increment(); }
    public void refreshFailure() { refreshFailure.increment(); }

    public <T> T timeTokenIssue(java.util.concurrent.Callable<T> callable) throws Exception {
        return tokenIssueTimer.recordCallable(callable);
    }
}
