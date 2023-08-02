import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.deserializedOrder.DeserializedOrder;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static base.Config.Urls.BASE_URI;
import static io.restassured.RestAssured.given;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.*;

public class ListOrdersTest {
    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    @DisplayName("Десереализую полученный JSON и проверяю, что списки не пустые - позитивный тест")
    public void checkThatDeserializedListsAreNotEmptyPositiveTest() {
        DeserializedOrder deserializedOrder = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders")
                .body().as(DeserializedOrder.class);

        assertFalse(deserializedOrder.orders.isEmpty());
        assertFalse(deserializedOrder.availableStations.isEmpty());
    }

    @Test
    @DisplayName("Проверяю статус код, и что список заказов не пустой - позитивный тест")
    public void checkResponseStatusCodeAndResponseListOrderAreNotEmptyPositiveTest() {
        ValidatableResponse responseOrders = given().header("Content-type", "application/json")
                .get("/api/v1/orders").then();

        int statusCode = responseOrders.extract().statusCode();
        assertEquals("Ожидаю статус код 200", HTTP_OK, statusCode);

        ArrayList<ValidatableResponse> responseList = responseOrders.extract().path("orders");
        assertFalse(responseList.isEmpty());
    }
}
