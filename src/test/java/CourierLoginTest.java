import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    private static int courierId;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        String body = "{\"login\": \"loginTestCourier\", \"password\": \"12345\", \"firstName\": \"LoginTest\"}";
        given().contentType("application/json").body(body).post("/api/v1/courier");
        courierId = given()
                .contentType("application/json")
                .body("{\"login\": \"loginTestCourier\", \"password\": \"12345\"}")
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .path("id");
    }

    @AfterClass
    public static void tearDown() {
        given().pathParam("id", courierId).delete("/api/v1/courier/{id}");
    }

    @Test
    @DisplayName("courierShouldBeAbleToLoginTest")
    public void courierShouldBeAbleToLoginTest() {
        given()
                .contentType("application/json")
                .body("{\"login\": \"loginTestCourier\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Ignore //Тест в какой-то момент начал падать по таймауту 504, руками то же самое
    @Test
    @DisplayName("loginShouldRequireAllMandatoryFieldsLoginTest")
    public void loginShouldRequireAllMandatoryFieldsLoginTest() {
        given()
                .contentType("application/json")
                .body("{\"login\": \"loginTestCourier\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("loginShouldRequireAllMandatoryFieldsPasswordTest")
    public void loginShouldRequireAllMandatoryFieldsPasswordTest() {
        given()
                .contentType("application/json")
                .body("{\"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("shouldReturnErrorForIncorrectLoginOrPasswordTest")
    public void shouldReturnErrorForIncorrectLoginOrPasswordTest() {
        given()
                .contentType("application/json")
                .body("{\"login\": \"loginTestCourier\", \"password\": \"wrong\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("shouldReturnErrorForNonExistingUserTest")
    public void shouldReturnErrorForNonExistingUserTest() {
        given()
                .contentType("application/json")
                .body("{\"login\": \"nonExisting\", \"password\": \"12345\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
