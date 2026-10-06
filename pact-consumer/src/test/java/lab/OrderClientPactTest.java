package lab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "orders-api", pactVersion = PactSpecVersion.V4)
class OrderClientPactTest {

    @Pact(consumer = "orders-web")
    V4Pact orderExists(PactDslWithProvider b) {
        var body = new PactDslJsonBody().integerType("id", 42L).stringMatcher("status", "NEW|PAID|SHIPPED", "NEW");
        return b.given("order 42 exists")
                .uponReceiving("get order 42")
                .path("/orders/42").method("GET")
                .willRespondWith().status(200).body(body)
                .toPact(V4Pact.class);
    }

    @Pact(consumer = "orders-web")
    V4Pact orderMissing(PactDslWithProvider b) {
        return b.given("order 99 does not exist")
                .uponReceiving("get missing order 99")
                .path("/orders/99").method("GET")
                .willRespondWith().status(404)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "orderExists")
    void readsExistingOrder(MockServer server) throws Exception {
        var order = new OrderClient(server.getUrl()).getOrder(42);
        assertEquals(42L, order.id());
        assertEquals("NEW", order.status());
    }

    @Test
    @PactTestFor(pactMethod = "orderMissing")
    void missingOrderIsNull(MockServer server) throws Exception {
        assertNull(new OrderClient(server.getUrl()).getOrder(99));
    }
}
