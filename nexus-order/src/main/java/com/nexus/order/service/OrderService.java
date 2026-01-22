package com.nexus.order.service;

import com.nexus.order.dto.OrderDTO;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    // A mágica do Spring Cloud AWS 3.0: template pronto para uso
    private final SqsTemplate sqsTemplate;

    public void placeOrder(OrderDTO order) {
        log.info("📦 Processando pedido para o produto ID: {}", order.productId());

        // Envia para a fila SQS definida no LocalStack (nexus-orders)
        sqsTemplate.send(to -> to
                .queue("nexus-orders") // IMPORTANTE: Esse nome deve bater com o init-aws.sh
                .payload(order)
        );

        log.info("✅ Pedido enviado para a fila SQS com sucesso!");
    }
}