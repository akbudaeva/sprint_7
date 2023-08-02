package client;

import io.restassured.response.ValidatableResponse;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private final String ORDER_URI = "api/v1/orders/";

    public ValidatableResponse createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDER_URI)
                .then();
    }

    public ValidatableResponse deleteOrder(int id) {
        return given()
                .header("Content-type", "application/json")
                .body(id)
                .when()
                .put(ORDER_URI + id)
                .then();
    }
}