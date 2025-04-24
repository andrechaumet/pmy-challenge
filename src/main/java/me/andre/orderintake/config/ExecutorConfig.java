package me.andre.orderintake.config;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// TODO:
//@ConfigurationProperties(prefix = "executor")
public class ExecutorConfig {

  @Value("${executor.max-pool-size}")
  private Integer maxPoolSize;
  @Value("${executor.core-pool-size}")
  private Integer corePoolSize;
  @Value("${executor.keep-alive-seconds}")
  private Long keepAliveSeconds;

  // TODO: PriorityBlockingQueue
  @Bean(name = "taskExecutor")
  public ExecutorService taskExecutor() {
    return new ThreadPoolExecutor(
        corePoolSize,
        maxPoolSize,
        keepAliveSeconds,
        SECONDS,
        new SynchronousQueue<>(),
        // TODO: fallback when saturated pool
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
}
