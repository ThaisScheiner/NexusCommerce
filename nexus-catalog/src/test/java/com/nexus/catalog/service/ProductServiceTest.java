package com.nexus.catalog.service;

import com.nexus.catalog.dto.ProductDTO;
import com.nexus.catalog.entity.Product;
import com.nexus.catalog.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private S3Client s3Client;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private ProductService service;

    @Test
    @DisplayName("Deve retornar produto do banco de dados quando não estiver no cache (Cache Miss)")
    void shouldReturnProductFromDb() {
        // --- ARRANGE (Preparação) ---
        String id = "prod-123";
        String bucketName = "nexus-test-bucket";
        // Simulando a entidade que viria do banco de dados
        Product entity = new Product(id, "iPhone 15", BigDecimal.valueOf(8000), "iphone-img.jpg");

        // Como não estamos subindo o Spring Context completo, usamos Reflection para injetar o valor de @Value("${aws.bucket}")
        ReflectionTestUtils.setField(service, "bucketName", bucketName);

        // Definindo comportamento do Mock: "Quando buscar pelo ID, retorne a entidade criada acima"
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(entity));

        // --- ACT (Ação) ---
        ProductDTO result = service.getProduct(id);

        // --- ASSERT (Validação) ---
        Assertions.assertNotNull(result);
        Assertions.assertEquals("iPhone 15", result.name());

        // Verifica se a URL simulada do S3 foi montada corretamente com o nome do bucket injetado
        // A lógica do método generatePresignedUrl deve usar o bucketName
        Assertions.assertTrue(result.imageUrl().contains(bucketName));

        // Verifica se o repositório foi chamado exatamente 1 vez
        Mockito.verify(repository, Mockito.times(1)).findById(id);
    }
}