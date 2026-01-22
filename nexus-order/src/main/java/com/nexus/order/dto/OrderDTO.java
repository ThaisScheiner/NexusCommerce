package com.nexus.order.dto;

import java.math.BigDecimal;

public record OrderDTO(
        String userId,
        String productId,
        Integer quantity,
        BigDecimal totalPrice
) {}