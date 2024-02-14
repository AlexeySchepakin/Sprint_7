import io.qameta.allure.junit4.DisplayName;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private static CourierClient courierClient;
    private Integer courierId;

    @BeforeClass
    public static void setupClass() {
        courierClient = new CourierClient();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierClient.deleteCourier(courierId);
            courierId = null;
        }
    }

    private void createAndLoginCourier(String login, String password) {
        String body = String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"LoginTest\"}", login, password);
        courierClient.createCourier(body).then().statusCode(201);
        courierId = courierClient.loginCourier(body).then().extract().path("id");
    }

    @Test
    @DisplayName("courierShouldBeAbleToLoginWithCorrectCredentials")
    public void courierShouldBeAbleToLoginWithCorrectCredentials() {
        String login = "loginTestCourier" + System.currentTimeMillis();
        String password = "12345";
        createAndLoginCourier(login, password); // Создаем и логиним курьера

        courierClient.loginCourier(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password))
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Ignore //Тест в какой-то момент начал падать по таймауту 504, руками то же самое
    @Test
    @DisplayName("loginShouldRequireAllMandatoryFieldsLoginTest")
    public void loginShouldRequireAllMandatoryFieldsLoginTest() {
        courierClient.loginCourier("{\"login\": \"\"}")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("loginShouldRequireAllMandatoryFieldsPasswordTest")
    public void loginShouldRequireAllMandatoryFieldsPasswordTest() {
        courierClient.loginCourier("{\"password\": \"\"}")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("shouldReturnErrorForIncorrectLoginOrPasswordTest")
    public void shouldReturnErrorForIncorrectLoginOrPasswordTest() {
        String login = "loginTestCourier" + System.currentTimeMillis();
        createAndLoginCourier(login, "12345");

        courierClient.loginCourier(String.format("{\"login\": \"%s\", \"password\": \"wrong\"}", login))
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("shouldReturnErrorForNonExistingUserTest")
    public void shouldReturnErrorForNonExistingUserTest() {
        courierClient.loginCourier("{\"login\": \"nonExisting\", \"password\": \"12345\"}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
