package com.nexus.catalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity // Indica que esta classe é uma tabela no banco de dados
@Table(name = "products") // Define o nome da tabela como "products"
@Data // Lombok: Gera Getters, Setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Gera construtor vazio (obrigatório para JPA)
@AllArgsConstructor // Lombok: Gera construtor com todos os argumentos
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gera IDs únicos automaticamente (UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    // Armazenamos apenas o "nome do arquivo" no S3 (ex: "cafe-mug.jpg"), não a URL completa
    @Column(name = "image_key")
    private String imageKey;
}