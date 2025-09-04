package com.manvanth.servenow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration for enabling auditing capabilities
 * This enables automatic population of @CreatedDate and @LastModifiedDate fields
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}