package org.training.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.training.vertx.comman.Endpoint;

import static io.vertx.core.http.HttpMethod.GET;

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

        //Метод для включения вертикали по обработке запросов времени
        httpRouter.route("/enableGetTimestamp")
                .method(GET)
                .handler(this::startModelVerticle);

        // Пример подключения метода для обработки http-запроса
        httpRouter.route("/getTimestamp")
                .method(GET)
                .handler(this::getTimestamp);

        return httpRouter;
    }

    private void startModelVerticle(RoutingContext context) {
        System.out.println("Received request to enable model verticle");
        vertx.deployVerticle(new ModelVerticle());
        HttpServerResponse response = context.response();
        response.end("Model verticle activated!");
    }

    /**
     * Метод, обрабатывающий http-запрос
     *
     * @param routingContext данные о http-запросе
     */
    private void getTimestamp(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        System.out.println("HttpServerVerticle::getTimestamp - request time");

        //Отправляем запрос времени
        eventBus.send(Endpoint.EB_BROADCAST, "Get timestamp", messageAsyncResult -> {
            //Дожидаемся ответа модели
            if(messageAsyncResult.succeeded()){
                System.out.println("Timestamp received");
                String body = messageAsyncResult.result().body().toString();
                response.end(body);
                System.out.println("Success sending response");
            } else {
                System.out.println("Response from model is not success!");
                response.end("Response from model is not success!");
            }
        });
    }
}
