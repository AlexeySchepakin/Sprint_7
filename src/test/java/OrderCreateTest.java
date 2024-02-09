import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final String[] color;

    public OrderCreateTest(String[] color) {
        this.color = color;
    }

    @BeforeClass
    public static void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/api/v1";
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
    @DisplayName("testCreateOrderWithVariousColorCombinations")
    public void testCreateOrderWithVariousColorCombinations() {
        given()
                .contentType("application/json")
                .body(buildOrderRequestBody(color))
                .when()
                .post("/orders")
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
