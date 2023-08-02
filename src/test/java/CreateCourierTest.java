import client.CourierClient;
import com.github.javafaker.Faker;
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
import static org.junit.Assert.*;

public class CreateCourierTest {
    private CourierClient courierClient;
    private int courierId;
    private int courierId2;


    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        courierClient = new CourierClient();

    }

    @After
    public void clearData() {
        courierClient.delete(courierId);
        courierClient.delete(courierId2);
    }

    @Test
    @DisplayName("Создаём курьера позитивный тест")
    public void createCourierPositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");

        //Проверяем, что курьер создан
        assertEquals("Некорректный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue("ID курьера не создан", isCourierCreated);

        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
        assertTrue("ID курьера не создан", courierId != 0);
    }

    @Test
    @DisplayName("Проверяем статус код ответа позитивный тест")
    public void checkStatusResponseCode200PositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаю курьера первый раз
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        assertEquals("Неверный статус код, ожидаю 201", HTTP_CREATED, statusCode);

        //Получаю данные курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

    }

    @Test
    @DisplayName("Успешный запрос позитивный тест")
    public void successfulRequestReturnsOkPositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаю курьера первый раз
        ValidatableResponse createResponse = courierClient.create(courier);
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertTrue(isCourierCreated);

        //Получаю данные курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");
    }


    @Test
    @DisplayName("Создаём 2 идентичных курьера негативный тест")
    public void creatingTwoIdenticalCouriersNegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаю курьера первый раз
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals("Неверный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);

        //Логинюсь
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        //создаю дубль курьера
        ValidatableResponse createResponse2 = courierClient.create(courier);
        int statusCode2 = createResponse2.extract().statusCode();

        String actualMessage409 = createResponse2.extract().path("message");
        String expectedMessage409 = "Этот логин уже используется. Попробуйте другой.";
        assertEquals("Неверный статус код, ожидаю 409", HTTP_CONFLICT, statusCode2);
        assertEquals("Ожидаю текст message and point создание курьера", expectedMessage409, actualMessage409);

    }

    @Test
    @DisplayName("Создаём курьера без логина негативный тест")
    public void requestWithoutLoginWillReturnStatusCode400NegativeTest() {
        Faker faker = new Faker();

        // Создали объект курьера без логина
        Courier courier = new Courier(faker.name().username(), faker.name().firstName());

        // Создаю курьера в ручке
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();

        if (statusCode == 400) {
            String actualMessage400 = createResponse.extract().path("message");
            String expectedMessage400 = "Недостаточно данных для создания учетной записи";

            assertEquals("Неверный статус код, ожидаю 400", HTTP_BAD_REQUEST, statusCode);
            assertEquals("Неверное сообщение об ошибке", expectedMessage400, actualMessage400);
        } else {
            ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
            courierId = loginResponse.extract().path("id");
        }

    }

    @Test
    @DisplayName("Создаём курьера без пароля негативный тест")
    public void requestWithOneLoginWillReturnStatusCode400NegativeTest() {
        Faker faker = new Faker();

        // Создали объект курьера без password и firstName
        Courier courier = new Courier(faker.name().username());

        // Создаю курьера в ручке
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();

        if (statusCode == 400) {
            String actualMessage400 = createResponse.extract().path("message");
            String expectedMessage400 = "Недостаточно данных для создания учетной записи";

            assertEquals("Неверный статус код, ожидаю 400", HTTP_BAD_REQUEST, statusCode);
            assertEquals("Неверное сообщение об ошибке", expectedMessage400, actualMessage400);
        } else {
            ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
            courierId = loginResponse.extract().path("id");
        }

    }

    @Test
    @DisplayName("Создаём 2-х курьеров с одинаковым логином негативный тест")
    public void createUserWithLoginThatAlreadyExistsAnErrorIsReturnedNegativeTest() {
        Faker faker = new Faker();

        // Создали объекты курьеров c одинаковым логином
        Courier courier = new Courier("ffff123", faker.name().username(), faker.name().firstName());
        Courier courier2 = new Courier("ffff123", faker.name().username(), faker.name().firstName());

        // Создаю курьера первый раз и проверяю, что он создан
        ValidatableResponse createResponse = courierClient.create(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean isCourierCreated = createResponse.extract().path("ok");
        assertEquals("Неверный статус код, ожидаю 201", HTTP_CREATED, statusCode);
        assertTrue(isCourierCreated);

        //Получаю данные курьера для удаления
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        // Создаю курьера в ручке с одинаковым логином и проверяю статус код ответа и тело ответа (негативный тест)
        ValidatableResponse createResponse2 = courierClient.create(courier2);
        int statusCode2 = createResponse2.extract().statusCode();

        String actualMessage409 = createResponse2.extract().path("message");
        String expectedMessage409 = "Этот логин уже используется. Попробуйте другой.";

        assertEquals("Неверный статус код, ожидаю 409", HTTP_CONFLICT, statusCode2);
        assertEquals("Неверное сообщение об ошибке", expectedMessage409, actualMessage409);

        //Получаю данные 2-го курьера, если он создаться по ошибке для удаления
        ValidatableResponse loginResponse2 = courierClient.login(CourierCredentials.from(courier2));
        int courierId2 = loginResponse.extract().path("id");
    }

}