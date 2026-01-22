package com.nexus.catalog.service;

import com.nexus.catalog.dto.ProductDTO;
import com.nexus.catalog.entity.Product;
import com.nexus.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository repository;
    private final S3Client s3Client; // Injetado via AwsConfig.java

    // Pega o nome do bucket do application.yml
    @Value("${aws.bucket}")
    private String bucketName;

    @Value("${aws.endpoint}")
    private String awsEndpoint;

    /**
     * Busca um produto pelo ID.
     * 1. Verifica se já existe no Redis (chave "products::<id>").
     * 2. Se não existir, vai no PostgreSQL.
     * 3. Salva no Redis automaticamente para a próxima vez.
     */
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProduct(String id) {
        log.info("🔍 Cache Miss - Buscando produto no Banco de Dados: {}", id);

        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));

        // Gera a URL pública para acessar a imagem no LocalStack
        String imageUrl = generatePublicUrl(product.getImageKey());

        return new ProductDTO(product.getId(), product.getName(), product.getPrice(), imageUrl);
    }

    /**
     * Cria um produto novo.
     * 1. Faz Upload da imagem para o S3.
     * 2. Salva os metadados no Banco.
     */
    @Transactional
    public ProductDTO createProduct(ProductDTO dto, byte[] imageBytes) {
        String imageKey = UUID.randomUUID().toString() + ".jpg";

        log.info("📤 Iniciando upload para S3 (Bucket: {}), Key: {}", bucketName, imageKey);

        // Upload para o S3 (LocalStack)
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageKey)
                        .build(),
                RequestBody.fromBytes(imageBytes)
        );

        // Salva no PostgreSQL
        Product entity = new Product(null, dto.name(), dto.price(), imageKey);
        Product saved = repository.save(entity);

        log.info("✅ Produto salvo com sucesso: ID {}", saved.getId());

        return new ProductDTO(
                saved.getId(),
                saved.getName(),
                saved.getPrice(),
                generatePublicUrl(imageKey)
        );
    }

    // Monta a URL para acessar o arquivo no LocalStack via navegador
    private String generatePublicUrl(String key) {
        // Formato: http://localhost:4566/<bucket-name>/<key>
        return awsEndpoint + "/" + bucketName + "/" + key;
    }
}