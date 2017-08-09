package org.training.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.spi.cluster.ignite.IgniteClusterManager;
import org.training.vertx.verticle.HttpServerVerticle;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Anton Lenok <AILenok.SBT@sberbank.ru>
 * @since 21.04.17.
 * Главное приложение запускающее дочерние Verticle
 */
public class MainApp {

    private final static int DEFAULT_CLUSTER_PORT = 47600;

    private static Vertx vertx;

    public static void main(String args[]) throws IOException {
        //Найдем все Ip адреса
        List<Inet4Address> inetAddresses = getNonLoopbackLocalIPv4Addresses();
        System.out.println("Available ipv4 addresses: " + Arrays.toString(inetAddresses.toArray()));
        //Отфильтруем ненужное
        String publicClusterHost = filterAddresses(inetAddresses);

        System.out.println("Public cluster host: " + publicClusterHost);

        VertxOptions options = new VertxOptions()
                .setClustered(true)
                .setClusterManager(getIgniteClusterManager())
                .setClusterHost(publicClusterHost)
                .setClusterPort(DEFAULT_CLUSTER_PORT);

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

    private static String filterAddresses(List<Inet4Address> inetAddresses) {
        for (Inet4Address address : inetAddresses) {
            if (!address.isLoopbackAddress()
                    && !address.getHostAddress().startsWith("192.168.")) {
                return address.getHostAddress();
            }
        }
        return null;
    }

    private static List<Inet4Address> getNonLoopbackLocalIPv4Addresses() throws IOException {
        List<Inet4Address> localAddresses = new ArrayList<>();

        Enumeration en = NetworkInterface.getNetworkInterfaces();
        while (en.hasMoreElements()) {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements(); ) {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                    localAddresses.add((Inet4Address) addr);
                }
            }
        }
        return localAddresses;
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
