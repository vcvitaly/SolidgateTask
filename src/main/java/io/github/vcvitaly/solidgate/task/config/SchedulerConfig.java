package io.github.vcvitaly.solidgate.task.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@EnableScheduling
public class SchedulerConfig {
}
