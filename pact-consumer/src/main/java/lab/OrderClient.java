package lab;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Consumer side: the "orders-web" app reads an order from the orders-api provider. */
public class OrderClient {
    public record Order(long id, String status) {}

    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
    private static final Pattern STATUS = Pattern.compile("\"status\"\\s*:\\s*\"([A-Z]+)\"");

    private final String baseUrl;
    private final HttpClient http = HttpClient.newHttpClient();

    public OrderClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Order getOrder(long id) throws IOException, InterruptedException {
        var req = HttpRequest.newBuilder(URI.create(baseUrl + "/orders/" + id))
                .header("Accept", "application/json").build();
        var res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 404) {
            return null;
        }
        Matcher i = ID.matcher(res.body());
        Matcher s = STATUS.matcher(res.body());
        if (!i.find() || !s.find()) {
            throw new IOException("unexpected body: " + res.body());
        }
        return new Order(Long.parseLong(i.group(1)), s.group(1));
    }
}
