package com.example.matchingms.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomRetryer implements Retryer {

    private final int maxAttempts;
    private final long maxDelay;
    private int attempt = 0;
    private long delay = 1000;

    public CustomRetryer() {
        this.maxAttempts = 5;
        this.maxDelay = 10000;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (++attempt >= maxAttempts) {
            log.error("Retry failed after {} attempts", attempt);
            throw e;
        }

        // For 429 errors, use exponential backoff with longer delays
        if (e.status() == 429) {
            delay = Math.min(delay * 2, maxDelay);
            log.warn("Rate limited (429), retrying in {}ms (attempt {}/{})", delay, attempt, maxAttempts);
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw e;
            }
        } else {
            delay = 1000;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw e;
            }
        }
    }

    @Override
    public Retryer clone() {
        return new CustomRetryer();
    }
}
