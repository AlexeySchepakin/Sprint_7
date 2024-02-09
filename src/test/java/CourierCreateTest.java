import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreateTest {

    private static final List<Integer> createdCouriersIds = new ArrayList<>();

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @AfterClass
    public static void tearDownClass() {
        for (Integer courierId : createdCouriersIds) {
            given().pathParam("id", courierId)
                    .delete("/api/v1/courier/{id}");
        }
    }
    private int courierId;
    @Test
    @DisplayName("shouldBeAbleToCreateCourierWithUniqueLoginTest")
    public void shouldBeAbleToCreateCourierWithUniqueLoginTest() {
        String uniqueLogin = "testCourier" + System.currentTimeMillis();
        String requestBody = String.format("{\"login\": \"%s\", \"password\": \"1234\", \"firstName\": \"Test\"}", uniqueLogin);

        given()
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Response loginResponse = given()
                .contentType("application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"1234\"}", uniqueLogin))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();

        courierId = loginResponse.jsonPath().getInt("id");
    }

    @After
    public void tearDownForUniqueLogin() {
        if (courierId != 0) {
            given()
                    .pathParam("id", courierId)
                    .when()
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(200);
        }
    }

    @Test
    @DisplayName("shouldNotCreateCourierWithDuplicateLoginTest")
    public void shouldNotCreateCourierWithDuplicateLoginTest() {
        String body = "{\"login\": \"duplicateCourier\", \"password\": \"1234\", \"firstName\": \"Duplicate\"}";
        given().contentType("application/json").body(body).post("/api/v1/courier");
        int id = getCourierId("duplicateCourier", "1234");
        createdCouriersIds.add(id);
        given().contentType("application/json").body(body)
                .when().post("/api/v1/courier")
                .then().statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("shouldRequireAllMandatoryFieldsToCreateCourierTest")
    public void shouldRequireAllMandatoryFieldsToCreateCourierTest() {
        given()
                .contentType("application/json")
                .body("{\"password\": \"1234\", \"firstName\": \"Name\"}")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    private int getCourierId(String login, String password) {
        Response loginResponse = given()
                .contentType("application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password))
                .post("/api/v1/courier/login");
        return loginResponse.jsonPath().getInt("id");
    }
}
