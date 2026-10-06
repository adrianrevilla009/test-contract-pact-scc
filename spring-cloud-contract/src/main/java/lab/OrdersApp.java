package lab;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/** Provider: orders-api. Its contracts live next to it and drive both its tests and the consumer stubs. */
@SpringBootApplication
public class OrdersApp {
    public static void main(String[] args) {
        SpringApplication.run(OrdersApp.class, args);
    }

    public record Order(long id, String status) {}

    @RestController
    public static class OrderController {
        private final Map<Long, Order> store = Map.of(42L, new Order(42, "NEW"));

        @GetMapping("/orders/{id}")
        public ResponseEntity<Order> get(@PathVariable long id) {
            Order o = store.get(id);
            return o == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(o);
        }
    }
}
