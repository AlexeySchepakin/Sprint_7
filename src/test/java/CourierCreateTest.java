import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CourierCreateTest {
    private static final List<Integer> createdCouriersIds = new ArrayList<>();
    private static final CourierClient courierClient = new CourierClient();
    private int courierId;

    @BeforeClass
    public static void setup() {
           }

    @AfterClass
    public static void tearDownClass() {
        for (Integer id : createdCouriersIds) {
            courierClient.deleteCourier(id);
        }
    }

    @Test
    @DisplayName("shouldBeAbleToCreateCourierWithUniqueLoginTest")
    public void shouldBeAbleToCreateCourierWithUniqueLoginTest() {
        String uniqueLogin = "testCourier" + System.currentTimeMillis();
        String requestBody = String.format("{\"login\": \"%s\", \"password\": \"1234\", \"firstName\": \"Test\"}", uniqueLogin);

        courierClient.createCourier(requestBody)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Response loginResponse = courierClient.loginCourier(String.format("{\"login\": \"%s\", \"password\": \"1234\"}", uniqueLogin));
        courierId = loginResponse.jsonPath().getInt("id");
        createdCouriersIds.add(courierId);
    }

    @After
    public void tearDownForUniqueLogin() {
        if (courierId != 0) {
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("shouldNotCreateCourierWithDuplicateLoginTest")
    public void shouldNotCreateCourierWithDuplicateLoginTest() {
        String duplicateLogin = "{\"login\": \"duplicateCourier\", \"password\": \"1234\", \"firstName\": \"Duplicate\"}";
        courierClient.createCourier(duplicateLogin);

        // Получаем ID первого курьера для последующего удаления
        Response loginResponse = courierClient.loginCourier("{\"login\": \"duplicateCourier\", \"password\": \"1234\"}");
        int id = loginResponse.jsonPath().getInt("id");
        createdCouriersIds.add(id);

        courierClient.createCourier(duplicateLogin)
                .then()
                .statusCode(409)
                .body("message", is("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("shouldRequireAllMandatoryFieldsToCreateCourierTest")
    public void shouldRequireAllMandatoryFieldsToCreateCourierTest() {
        String requestBodyMissingLogin = "{\"password\": \"1234\", \"firstName\": \"Name\"}";

        courierClient.createCourier(requestBodyMissingLogin)
                .then()
                .statusCode(400)
                .body("message", is("Недостаточно данных для создания учетной записи"));
    }
}
