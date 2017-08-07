package org.training.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.training.vertx.comman.Endpoint;

/**
 * @author Anton Lenok <AILenok.SBT@sberbank.ru>
 * @since 21.04.17.
 * Verticle запускающий внутри себя HTTP сервер
 */
public class HttpServerVerticle extends AbstractVerticle {

    private EventBus eventBus;

    /**
     * Метод start запускатеся при старке Verticle-класса
     */
    @Override
    public void start() {
        eventBus = vertx.eventBus();

        // Создаем http-сервер с помощью Vert.x
        HttpServer httpServer = vertx.createHttpServer();

        // Создаем сложную логику обработки http-запросов
        Router httpRouter = getRouter();

        // Задаем логику образоботку запросов http-серверу
        httpServer.requestHandler(httpRouter::accept);

        // Запускам http-сервер на 8080 порту
        httpServer.listen(8080);
    }

    /**
     * Метод, который аккамулирует обработку http-методов
     *
     * @return Router-объект обработчик http-запросов
     */
    private Router getRouter() {
        Router httpRouter = Router.router(vertx);

        // Пример подключения метода для обработки http-запроса
        httpRouter.route("/").handler(this::broadcast);

        return httpRouter;
    }

    /**
     * Метод, обрабатывающий http-запрос
     *
     * @param routingContext данные о http-запросе
     */
    private void broadcast(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        System.out.println("HttpServerVerticle::broadcast - request time");

        eventBus.publish(Endpoint.EB_BROADCAST, "Send message broadcast");
    }
}
