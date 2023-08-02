package client;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class GetOrderByNumber {
    private final String GET_ORDER_BY_NUMBER_URI = "api/v1/orders/track";

    public ValidatableResponse getOrder(String qName, int qValue) {
        return given()
                .queryParam(qName, qValue)
                .get(GET_ORDER_BY_NUMBER_URI)
                .then();
    }

    public ValidatableResponse getOrder() {
        return given()
                .get(GET_ORDER_BY_NUMBER_URI)
                .then();
    }
}
