package com.nexus.catalog.repository;

import com.nexus.catalog.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    // Graças ao Spring Data JPA, não precisamos implementar nada aqui.
    // Ele cria o SQL automaticamente em tempo de execução.

    // Se precisar buscar por nome no futuro, bastaria adicionar:
    // List<Product> findByName(String name);
}