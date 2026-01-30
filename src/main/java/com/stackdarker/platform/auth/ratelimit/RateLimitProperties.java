package com.stackdarker.platform.auth.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "platform.rate-limit")
public class RateLimitProperties {

    private boolean enabled = true;

    private BucketSpec api = new BucketSpec(120, 120, 60);
    private BucketSpec unauthenticatedApi = new BucketSpec(60, 60, 60);

    private AuthSpec auth = new AuthSpec();

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public BucketSpec getApi() { return api; }
    public void setApi(BucketSpec api) { this.api = api; }

    public BucketSpec getUnauthenticatedApi() { return unauthenticatedApi; }
    public void setUnauthenticatedApi(BucketSpec unauthenticatedApi) { this.unauthenticatedApi = unauthenticatedApi; }

    public AuthSpec getAuth() { return auth; }
    public void setAuth(AuthSpec auth) { this.auth = auth; }

    public static class AuthSpec {
        private BucketSpec login = new BucketSpec(10, 10, 60);
        private BucketSpec register = new BucketSpec(5, 5, 60);
        private BucketSpec refresh = new BucketSpec(20, 20, 60);

        public BucketSpec getLogin() { return login; }
        public void setLogin(BucketSpec login) { this.login = login; }

        public BucketSpec getRegister() { return register; }
        public void setRegister(BucketSpec register) { this.register = register; }

        public BucketSpec getRefresh() { return refresh; }
        public void setRefresh(BucketSpec refresh) { this.refresh = refresh; }
    }

    public static class BucketSpec {
        private int capacity;
        private int refillTokens;
        private int refillPeriodSeconds;

        public BucketSpec() {}

        public BucketSpec(int capacity, int refillTokens, int refillPeriodSeconds) {
            this.capacity = capacity;
            this.refillTokens = refillTokens;
            this.refillPeriodSeconds = refillPeriodSeconds;
        }

        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }

        public int getRefillTokens() { return refillTokens; }
        public void setRefillTokens(int refillTokens) { this.refillTokens = refillTokens; }

        public int getRefillPeriodSeconds() { return refillPeriodSeconds; }
        public void setRefillPeriodSeconds(int refillPeriodSeconds) { this.refillPeriodSeconds = refillPeriodSeconds; }
    }
}
