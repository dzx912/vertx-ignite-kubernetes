package org.training.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import org.training.vertx.comman.Endpoint;

/**
 * @author Anton Lenok <AILenok.SBT@sberbank.ru>
 * @since 26.04.17.
 * Verticle который символизирует фундаментальные вычисления с моделью данных
 */
public class ModelVerticle extends AbstractVerticle {

    private EventBus eventBus;

    @Override
    public void start() {
        eventBus = vertx.eventBus();

        eventBus.consumer(Endpoint.EB_BROADCAST, message -> {
            System.out.println("Model catch request for timestamp: " + message.body());
            String response = String.valueOf(System.currentTimeMillis());
            System.out.println("Timestamp generated: " + response);
            message.reply(response);
            System.out.println("Success replied with generated timestamp: " + response);
        });

        System.out.println("Model verticle started!");
    }
}
