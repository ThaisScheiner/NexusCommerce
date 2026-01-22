package com.nexus.catalog.dto;

import java.math.BigDecimal;

public record ProductDTO(
        String id,
        String name,
        BigDecimal price,
        String imageUrl
) {}