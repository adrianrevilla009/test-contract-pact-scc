package lab;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

/** Verifies the pact file in src/test/resources/pacts against a real OrdersApi on a random port. */
@Provider("orders-api")
@PactFolder("pacts")
@ExtendWith(PactVerificationInvocationContextProvider.class)
class OrdersApiProviderTest {
    OrdersApi api;

    @BeforeEach
    void start(PactVerificationContext ctx) throws Exception {
        api = new OrdersApi(0);
        api.start();
        ctx.setTarget(new HttpTestTarget("127.0.0.1", api.port()));
    }

    @AfterEach
    void stop() { api.stop(); }

    @TestTemplate
    void verify(PactVerificationContext ctx) { ctx.verifyInteraction(); }

    @State("order 42 exists")
    void order42() { api.orders.put(42L, "NEW"); }

    @State("order 99 does not exist")
    void noOrder99() { api.orders.remove(99L); }
}
