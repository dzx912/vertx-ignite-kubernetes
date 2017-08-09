package org.training.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.training.vertx.verticle.HttpServerVerticle;

import java.io.File;
import java.net.URL;

/**
 * @author Anton Lenok <AILenok.SBT@sberbank.ru>
 * @since 21.04.17.
 * Главное приложение запускающее дочерние Verticle
 */
public class MainApp {

    private static Vertx vertx;

    public static void main(String args[]) {
        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(getIgniteClusterManager());

        Vertx.clusteredVertx(options, vertxAsyncResult -> {
            if (vertxAsyncResult.succeeded()) {
                // Создаем главный объект vertx, который отвечает за сетевое взаимодействие
                vertx = vertxAsyncResult.result();

                deploy(vertx);
            } else {
                System.out.println("Can't create cluster");
                System.exit(1);
            }
        });
    }

    private static void deploy(Vertx vertx) {
        // Подключаем verticle к системе
        System.out.println("Deploy Verticles");
        vertx.deployVerticle(new HttpServerVerticle());
    }

    private static IgniteClusterManager getIgniteClusterManager() {
        try {
            String fileName = "ignite.xml";
            File configFile = new File(fileName);
            URL configUrl = configFile.toURI().toURL();

            return new IgniteClusterManager(configUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
