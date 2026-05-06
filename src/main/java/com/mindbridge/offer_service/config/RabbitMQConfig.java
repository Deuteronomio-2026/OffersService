package com.mindbridge.offer_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.offer-created}")
    private String offerCreatedRoutingKey;

    // Exchange principal
    @Bean
    public TopicExchange mindbridgeExchange() {
        return new TopicExchange(exchange);
    }

    // Cola para notificaciones de ofertas
    @Bean
    public Queue offerCreatedQueue() {
        return new Queue("offer.created.queue", true);
    }

    // Binding: conecta la cola al exchange con el routing key
    @Bean
    public Binding offerCreatedBinding() {
        return BindingBuilder
                .bind(offerCreatedQueue())
                .to(mindbridgeExchange())
                .with(offerCreatedRoutingKey);
    }

    // Convertidor JSON para los mensajes
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public Queue offerCancelledQueue() {
        return new Queue("offer.cancelled.queue", true);
    }

    @Bean
    public Queue offerTakenQueue() {
        return new Queue("offer.taken.queue", true);
    }

    @Bean
    public Binding offerCancelledBinding() {
        return BindingBuilder
                .bind(offerCancelledQueue())
                .to(mindbridgeExchange())
                .with("offer.cancelled");
    }

    @Bean
    public Binding offerTakenBinding() {
        return BindingBuilder
                .bind(offerTakenQueue())
                .to(mindbridgeExchange())
                .with("offer.taken");
    }
}
