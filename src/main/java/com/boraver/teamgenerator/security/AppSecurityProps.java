package com.boraver.teamgenerator.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record AppSecurityProps(String jwtSecret, long jwtExpirationMs) {}
