package lab;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;

/** Base class for the tests generated from src/test/resources/contracts. */
public abstract class BaseContractTest {
    @BeforeEach
    void setup() {
        RestAssuredMockMvc.standaloneSetup(new OrdersApp.OrderController());
    }
}
