package com.wongweiye.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

// this configuration refer to
// rsa:
//  public-key: classpath:certs/public.pem
//  private-key: classpath:certs/private.pem
@ConfigurationProperties(prefix = "rsa")
public record RSAKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
