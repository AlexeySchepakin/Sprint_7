import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

public class OrderListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = Configuration.BASE_URI;
    }
    @Test
    @DisplayName("testGetOrderList")
    public void testGetOrderList() {
        given()
                .when()
                .get("/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", not(empty()));
    }
}
