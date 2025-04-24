package me.andre.orderintake.config;

import java.util.HashMap;
import java.util.Map;
import me.andre.orderintake.exception.FailedToStoreOrderException;
import me.andre.orderintake.exception.LockTimeoutException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@EnableRetry
@Configuration
public class RetryConfig {

  @Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplate();

    FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
    fixedBackOffPolicy.setBackOffPeriod(2000L);
    retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

    Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>(1);
    exceptions.put(FailedToStoreOrderException.class, true);
    exceptions.put(LockTimeoutException.class, true);
    SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptions);

    retryTemplate.setRetryPolicy(retryPolicy);
    return retryTemplate;
  }
}