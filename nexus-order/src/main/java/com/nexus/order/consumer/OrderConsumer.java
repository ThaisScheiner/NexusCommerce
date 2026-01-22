package com.nexus.order.consumer;

import com.nexus.order.dto.OrderDTO;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderConsumer {

    // A anotação @SqsListener faz a mágica.
    // Ela fica monitorando a fila "nexus-orders" constantemente.
    @SqsListener("nexus-orders")
    public void listen(OrderDTO order) {
        log.info("📨 CONSUMER: Mensagem capturada da fila SQS!");

        try {
            log.info("⚙️ Processando pedido para o produto: {}", order.productId());
            log.info("💰 Valor total: R$ {}", order.totalPrice());

            // Simula um processamento demorado (ex: baixa no estoque, cobrança no cartão)
            Thread.sleep(2000);

            log.info("✅ Pedido processado e Nota Fiscal gerada para o usuário: {}", order.userId());

        } catch (InterruptedException e) {
            log.error("❌ Erro ao processar pedido", e);
            Thread.currentThread().interrupt();
        }
    }
}