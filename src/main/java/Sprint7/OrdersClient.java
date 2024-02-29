package Sprint7;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class OrdersClient {
    public OrdersClient() {
    }

    @Step("Create order")
    public Response createOrder(Object body) {
        return RestAssured.given()
                .baseUri(Configuration.BASE_URI)
                .basePath(Configuration.BASE_PATH)
                .header("Content-type", "application/json")
                .body(body)
                .when()
                .post("/orders");
    }
}
