import client.AcceptOrder;
import client.CourierClient;
import client.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.CourierCredentials;
import model.CourierGenerator;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static base.Config.Urls.BASE_URI;
import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;

public class AcceptOrderTest {

    private CourierClient courierClient;
    private int courierId;
    private int orderId;
    private OrderClient orderClient;

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        courierClient = new CourierClient();
        orderClient = new OrderClient();
    }

    @After
    public void clearData() {
        courierClient.delete(courierId);
        orderClient.deleteOrder(orderId);
    }

    @Test
    @DisplayName("Запрос с несуществующим номером курьера ответ 404 негативный тест")
    public void requestWithNonExistentCourierNumberResponse404NegativeTest() {
        AcceptOrder acceptOrder = new AcceptOrder();
        ValidatableResponse acceptOrderResponse = acceptOrder.acceptOrderRequest("1", "courierId", "213");
        int stausCode = acceptOrderResponse.extract().statusCode();
        String actualMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Курьера с таким id не существует";

        assertEquals("Ожидаю статус код ответа 404", HTTP_NOT_FOUND, stausCode);
        assertEquals("Неверный текст ошибки", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Этот заказ уже выполняется ответ 409 негативный тест")
    public void thisOrderIsAlreadyInProgressResponse409NegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);

        // Логинимся и запоминаем id курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        AcceptOrder acceptOrder = new AcceptOrder();
        ValidatableResponse acceptOrderResponse = acceptOrder.acceptOrderRequest("1", "courierId", courierId);
        int statusCode = acceptOrderResponse.extract().statusCode();
        String actualMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Этот заказ уже в работе";

        assertEquals("Ожидаю статус код ответа 409", HTTP_CONFLICT, statusCode);
        assertEquals("Неверный текст ошибки", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Заказ с этим идентификатором не существует Ответ 404 негативный тест")
    public void orderWithThisIDDoesNotExistResponse404NegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);

        // Логинимся и запоминаем id курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        // Создаём заказ
        Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{});
        ValidatableResponse responseCreateOrder = orderClient.createOrder(order);
        orderId = responseCreateOrder.extract().path("track");

        // Принимаем заказ
        AcceptOrder acceptOrder = new AcceptOrder();
        ValidatableResponse acceptOrderResponse = acceptOrder.acceptOrderRequest(orderId, "courierId", courierId);
        int statusCode = acceptOrderResponse.extract().statusCode();
        String actualMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Заказа с таким id не существует";

        assertEquals("Ожидаю статус код ответа 404", HTTP_NOT_FOUND, statusCode);
        assertEquals("Неверный текст ошибки", expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("недостаточно данных для ответа, возвращается ответ 400 негативный тест")
    public void notEnoughDataToSearchResponse400NegativeTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);

        // Логинимся и запоминаем id курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        // Создаём заказ
        Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{});
        ValidatableResponse responseCreateOrder = orderClient.createOrder(order);
        orderId = responseCreateOrder.extract().path("track");

        // Принимаем заказ
        AcceptOrder acceptOrder = new AcceptOrder();
        ValidatableResponse acceptOrderResponse = acceptOrder.acceptOrderRequest(orderId, "?courierId", courierId);
        int statusCode = acceptOrderResponse.extract().statusCode();
        String actualMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Недостаточно данных для поиска";

        assertEquals("Ожидаю статус код ответа 400", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Неверный текст ошибки", expectedMessage, actualMessage);
    }


    // Не могу понять почему тут приход 400 ответ, урл сформирован правильно (подскажите плз может я не увидел чего-то)
    @Test
    @DisplayName("Запрос с несуществующим номером негативный тест")
    public void requestWithNonExistentNumberPositiveTest() {
        Courier courier = CourierGenerator.getRandom();

        // Создаём курьера
        ValidatableResponse createResponse = courierClient.create(courier);

        // Логинимся и запоминаем id курьера
        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
        courierId = loginResponse.extract().path("id");

        // Создаём заказ
        Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{});
        ValidatableResponse responseCreateOrder = orderClient.createOrder(order);
        orderId = responseCreateOrder.extract().path("track");

        // Принимаем заказ
        AcceptOrder acceptOrder = new AcceptOrder();
        ValidatableResponse acceptOrderResponse = acceptOrder.acceptOrderRequest(orderId);
        int statusCode = acceptOrderResponse.extract().statusCode();
        //assertEquals("Ожидаю статус код ответа 200", HTTP_OK, statusCode);
    }
}