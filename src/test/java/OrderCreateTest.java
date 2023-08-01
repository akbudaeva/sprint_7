import client.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static base.Config.Urls.BASE_URI;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final String[] color;
    private OrderClient orderClient;
    private int orderId;

    public OrderCreateTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getOrder() {
        return new Object[][]{
                {new String[]{"BLACK", "GRAY"}},
                {new String[]{"BLACK"}},
                {new String[]{"GRAY"}},
                {new String[]{}},
        };
    }

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        orderClient = new OrderClient();
    }

    @After
    public void clear() {
        orderClient.deleteOrder(orderId);
    }

    @Test
    @DisplayName("Создаю заказ - позитивный тест")
    public void createOrderAndCheckStatusCodeAndGetTrackNumberPositiveTest() {
        Order order = new Order("Naruto", "Uchiha", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", color);

        ValidatableResponse responseCreateOrder = orderClient.createOrder(order);
        int statusCode = responseCreateOrder.extract().statusCode();
        int trackNumber = responseCreateOrder.extract().path("track");

        orderId = trackNumber;

        assertEquals("Ожидаю статус код 201", HTTP_CREATED, statusCode);
        assertTrue(trackNumber != 0);
    }
}