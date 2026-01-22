package com.nexus.catalog.controller;

import com.nexus.catalog.dto.ProductDTO;
import com.nexus.catalog.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    // GET: Busca produto (Lê do Redis ou Banco + URL S3)
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(service.getProduct(id));
    }

    // POST: Cria produto com Upload de Imagem
    // Exemplo Postman: Body -> form-data
    // Key: "image" (File), Key: "name" (Text), Key: "price" (Text)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(
            @RequestPart("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("price") BigDecimal price
    ) throws IOException {

        // Monta o DTO inicial
        var dto = new ProductDTO(null, name, price, null);

        // Chama o serviço passando os bytes da imagem
        var created = service.createProduct(dto, image.getBytes());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}