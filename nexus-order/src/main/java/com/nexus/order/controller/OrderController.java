package com.nexus.order.controller;

import com.nexus.order.dto.OrderDTO;
import com.nexus.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderDTO order) {
        // Dispara o processo (Fire-and-Forget)
        service.placeOrder(order);

        // Retorna 202 (Accepted) porque o processamento real será feito depois (assíncrono)
        return ResponseEntity.accepted()
                .body("Pedido recebido com sucesso! Você será notificado quando processado.");
    }
}