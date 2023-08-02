import client.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierCredentials;
import model.CourierGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static base.Config.Urls.BASE_URI;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LoginCourierTest {
    private CourierClient courierClient;
    private int courierId;

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        courierClient = new CourierClient();

    }

    @After
    public void clearData() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Создаю курьера и логинюсь- позитивный тест")
    public void createCourierPositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");

        //Проверяем, что курьер создан
        assertEquals("Некорректный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue("ID курьера не создан", isCourierCreated);

        //Авторизуемся и получаем id пользователя и проверяем, что id не пустой
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertTrue("ID курьера не создан", courierId != 0);
    }

    @Test
    @DisplayName("Авторизуюсь без логина, а потом без пароля - негативный тест")
    public void authorizationInvalidLoginAndPasswordReturnStatusCode400NegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");

        //Проверяем, что курьер создан
        assertEquals("Некорректный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue("ID курьера не создан", isCourierCreated);

        //Авторизуемся без пароля и проверяем статус код ошибки и текст ошибки
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.getLogin(courier));
        int statusCode400 = loginResponse.extract().statusCode();
        String actualMessageError400 = loginResponse.extract().path("message");
        String expectedMessageError400 = "Недостаточно данных для входа";
        assertEquals("Некорректный статус код, ожидаю 400", HTTP_BAD_REQUEST, statusCode400);
        assertEquals("Некорректный ответ ошибки", expectedMessageError400, actualMessageError400);

        //Авторизуемся без логина и проверяем статус код ошибки и текст ошибки
        ValidatableResponse loginResponse2 = courierClient.login(CourierCredentials.getPassword(courier));
        int statusCode2 = loginResponse2.extract().statusCode();
        String actual2MessageError400 = loginResponse2.extract().path("message");
        String expecte2dMessageError400 = "Недостаточно данных для входа";
        assertEquals("Некорректный статус код, ожидаю 400", HTTP_BAD_REQUEST, statusCode2);
        assertEquals("Некорректный ответ ошибки", expecte2dMessageError400, actual2MessageError400);

        //Авторизуемся и получаем id пользователя, чтобы подчистить пользователя после теста
        ValidatableResponse loginResponse3 = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse3.extract().path("id");
    }

    @Test
    @DisplayName("Авторизую курьера без создания учётной записи - негативный тест")
    public void authorizationUnderNonExistentUserReturn404NegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        //Авторизуемся, получаем статус код ответа и проверяем
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        int statusCode = loginResponse.extract().statusCode();
        String actualMessage404 = loginResponse.extract().path("message");
        String expectedMessage404 = "Учетная запись не найдена";
        assertEquals("Ожидаю статус код 404",HTTP_NOT_FOUND, statusCode);
        assertEquals("Неверное сообщение об ошибке 404",expectedMessage404, actualMessage404);
    }


}