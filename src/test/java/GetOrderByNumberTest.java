import client.CourierClient;
import client.GetOrderByNumber;
import client.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
import static base.Config.Urls.BASE_URI;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GetOrderByNumberTest {
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
        orderClient.deleteOrder(orderId);
    }

    @Test
    @DisplayName("Запрос с непустым номером позитивный тест")
    public void requestWithNonExistentNumberPositiveTest() {
        // Создаём заказ
        Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", new String[]{});
        ValidatableResponse responseCreateOrder = orderClient.createOrder(order);
        orderId = responseCreateOrder.extract().path("track");

        // Получаем заказ по номеру
        GetOrderByNumber getOrderByNumber = new GetOrderByNumber();
        ValidatableResponse acceptOrderResponse = getOrderByNumber.getOrder("t", orderId);
        int statusCode = acceptOrderResponse.extract().statusCode();
        assertEquals("Ожидаю статус код ответа 200", HTTP_OK, statusCode);

        acceptOrderResponse.assertThat().body("order", notNullValue());
    }

    @Test
    @DisplayName("Запрос с пустым трекером заказа негативный тест")
    public void requestWithoutAnOrderNumberReturnsAnError400NegativeTest() {
        // Получаем заказ по номеру
        GetOrderByNumber getOrderByNumber = new GetOrderByNumber();
        ValidatableResponse acceptOrderResponse = getOrderByNumber.getOrder();
        int statusCode = acceptOrderResponse.extract().statusCode();
        String responseMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Недостаточно данных для поиска";

        assertEquals("Ожидаю статус код ответа 400", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Неверное сообщение об ошибки", expectedMessage, responseMessage);
    }

    @Test
    @DisplayName("Запрос с неверным трекером заказа негативный тест")
    public void requestWithNonExistentNumberError404NegativeTest() {
        // Получаем заказ по номеру
        GetOrderByNumber getOrderByNumber = new GetOrderByNumber();
        ValidatableResponse acceptOrderResponse = getOrderByNumber.getOrder("t", 123456789);
        int statusCode = acceptOrderResponse.extract().statusCode();
        String responseMessage = acceptOrderResponse.extract().path("message");
        String expectedMessage = "Заказ не найден";

        assertEquals("Ожидаю статус код ответа 404", HTTP_NOT_FOUND, statusCode);
        assertEquals("Неверное сообщение об ошибки", expectedMessage, responseMessage);
    }

}