package com.nexus.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class AwsConfig {

    // Lê as variáveis do application.yml
    @Value("${aws.endpoint}") // http://localhost:4566
    private String endpoint;

    @Value("${aws.region}") // us-east-1
    private String region;

    @Value("${aws.access-key}") // test
    private String accessKey;

    @Value("${aws.secret-key}") // test
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // 1. Redireciona a chamada da AWS real para o nosso Docker (LocalStack)
                .endpointOverride(URI.create(endpoint))

                // 2. Define a região (ex: us-east-1)
                .region(Region.of(region))

                // 3. Define credenciais falsas (o LocalStack aceita qualquer coisa, mas precisa passar)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))

                // 4. [MUITO IMPORTANTE] Força o estilo de caminho (Path Style)
                // Na AWS real: https://bucket-name.s3.amazonaws.com (Virtual Hosted Style)
                // No LocalStack: http://localhost:4566/bucket-name (Path Style)
                // Sem isso, ele tenta conectar em "bucket.localhost" e dá erro de DNS.
                .forcePathStyle(true)

                .build();
    }
}