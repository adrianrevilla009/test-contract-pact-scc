package lab;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/** Provider side: the "orders-api" service, a JDK HttpServer with an in-memory store. */
public class OrdersApi {
    public final Map<Long, String> orders = new ConcurrentHashMap<>();
    private final HttpServer server;

    public OrdersApi(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/orders/", ex -> {
            String idPart = ex.getRequestURI().getPath().substring("/orders/".length());
            String status = idPart.matches("\\d+") ? orders.get(Long.parseLong(idPart)) : null;
            byte[] body = status == null ? new byte[0]
                    : ("{\"id\":" + idPart + ",\"status\":\"" + status + "\"}").getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(status == null ? 404 : 200, body.length == 0 ? -1 : body.length);
            if (body.length > 0) {
                ex.getResponseBody().write(body);
            }
            ex.close();
        });
    }

    public void start() { server.start(); }

    public void stop() { server.stop(0); }

    public int port() { return server.getAddress().getPort(); }

    public static void main(String[] args) throws IOException {
        var api = new OrdersApi(8080);
        api.orders.put(42L, "NEW");
        api.start();
    }
}
