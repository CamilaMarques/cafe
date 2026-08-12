package com.aromaorigem.aromaorigem.messaging;

import com.aromaorigem.aromaorigem.model.Assinatura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AssinaturaProducer {
    private static final String TOPIC = "assinatura-criada-topic";

    @Autowired
    private KafkaTemplate<String, Assinatura> kafkaTemplate;

    public void enviarEventoAssinatura(Assinatura assinatura) {
        kafkaTemplate.send(TOPIC, assinatura);
        System.out.println("☕ [KAFKA EVENTO] Assinatura enviada para o tópico: ID " + assinatura.getId());
    }
}
