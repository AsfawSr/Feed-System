package com.asfaw.feed_system.feed.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@Slf4j
public class FanOutAsyncConfig {

	@Value("${feedsystem.fanout.worker-pool-size:4}")
	private int workerPoolSize;

	@Value("${feedsystem.fanout.batch-size:500}")
	private int batchSize;

	@Bean(name = "fanOutExecutor")
	public Executor fanOutExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(workerPoolSize);
		executor.setMaxPoolSize(workerPoolSize * 2);
		executor.setQueueCapacity(1000);
		executor.setThreadNamePrefix("fan-out-worker-");
		executor.setRejectedExecutionHandler((r, executor1) -> {
			log.warn("Fan-out task rejected, executing in caller thread");
			r.run();
		});
		executor.initialize();
		return executor;
	}

	public int getBatchSize() {
		return batchSize;
	}
}
