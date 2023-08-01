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
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeleteCourierTest {
    CourierClient courierClient;
    int courierId;

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
    @DisplayName("Успешное удаление курьера позитивный тест")
    public void successfulCourierRemovalRequestPositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");

        //Проверяем, что курьер создан
        assertEquals("Некорректный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue("ID курьера не создан", isCourierCreated);

        // Логинимся и получаем id курьера для удаления
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        // Удаляем курьера и проверяем статус код и тело ответа
        ValidatableResponse deleteCourier = courierClient.delete(courierId);
        int deleteStatusCode = deleteCourier.extract().statusCode();
        boolean responseOk = deleteCourier.extract().path("ok");
        assertEquals("Ожидаю статус код 200 ", HTTP_OK, deleteStatusCode);
        assertTrue(responseOk);
    }

    @Test
    @DisplayName("Запрос с неверным ID негативный тест")
    public void requestWithoutIdReturnStatusCode404NegativeTest() {
        // Удаляем курьера и проверяем статус код и тело ответа
        ValidatableResponse deleteCourier = courierClient.delete(43);

        int StatusCode = deleteCourier.extract().statusCode();
        String actualMessage = deleteCourier.extract().path("message");
        String expectedMessage = "Курьера с таким id нет.";

        assertEquals("Ожидаю статус код 404 ", HTTP_NOT_FOUND, StatusCode);
        assertEquals("Неверный текст ответа", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Запрос без ID негативный тест")
    public void requestNotIdReturn404NegativeTest() {
        // Удаляем курьера и проверяем статус код и тело ответа
        ValidatableResponse deleteCourier = courierClient.delete();

        int StatusCode = deleteCourier.extract().statusCode();
        String actualMessage = deleteCourier.extract().path("message");
        String expectedMessage = "Not Found.";

        assertEquals("Ожидаю статус код 404 ", HTTP_NOT_FOUND, StatusCode);
        assertEquals("Неверный текст ответа", expectedMessage, actualMessage);
    }

}