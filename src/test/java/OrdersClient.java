import io.qameta.allure.Step;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class OrdersClient {
    public OrdersClient() {
    }

    @Step("Create order")
    public Response createOrder(Object body) {
        return given()
                .baseUri(Configuration.BASE_URI)
                .basePath(Configuration.BASE_PATH)
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/orders");
    }
}
