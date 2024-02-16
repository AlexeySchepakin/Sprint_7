package Sprint7tests;

import Sprint7.*;
import io.qameta.allure.junit4.DisplayName;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private static OrdersClient ordersClient;
    private final String[] color;

    public OrderCreateTest(String[] color) {
        this.color = color;
    }

    @BeforeClass
    public static void setUp() {
        ordersClient = new OrdersClient();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> colorCombinations() {
        return Arrays.asList(new Object[][] {
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}},
                {null}
        });
    }

    @Test
    @DisplayName("Create order with various color combinations")
    public void testCreateOrderWithVariousColorCombinations() {
        ordersClient.createOrder(buildOrderRequestBody(color))
                .then()
                .assertThat()
                .statusCode(201)
                .body("track", notNullValue());
    }

    private String buildOrderRequestBody(String[] color) {
        StringBuilder colorJsonPart = new StringBuilder();
        if (color != null && color.length > 0) {
            colorJsonPart.append(", \"color\": [");
            for (int i = 0; i < color.length; i++) {
                colorJsonPart.append("\"").append(color[i]).append("\"");
                if (i < color.length - 1) colorJsonPart.append(", ");
            }
            colorJsonPart.append("]");
        } else if (color == null) {
            colorJsonPart = new StringBuilder();
        } else {
            colorJsonPart.append(", \"color\": []");
        }

        return "{" +
                "\"firstName\": \"Naruto\"," +
                "\"lastName\": \"Uchiha\"," +
                "\"address\": \"Konoha, 142 apt.\"," +
                "\"metroStation\": 4," +
                "\"phone\": \"+7 800 555 35 35\"," +
                "\"rentTime\": 5," +
                "\"deliveryDate\": \"2020-06-06\"," +
                "\"comment\": \"Saske, come back to Konoha\"" +
                colorJsonPart +
                "}";
    }
}
