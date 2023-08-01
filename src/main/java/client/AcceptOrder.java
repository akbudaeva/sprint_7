package client;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class AcceptOrder {
    private final String BASE_ACCEPT_ORDER_URI = "api/v1/orders/accept/";


    public ValidatableResponse acceptOrderRequest(String idOrder, String qKey, String qValue) {
        return given()
                .queryParam(qKey, qValue)
                .put(BASE_ACCEPT_ORDER_URI + idOrder)
                .then();
    }

    public ValidatableResponse acceptOrderRequest(String idOrder, String qKey, int qValue) {
        return given()
                .queryParam(qKey, qValue)
                .put(BASE_ACCEPT_ORDER_URI + idOrder)
                .then();
    }
    public ValidatableResponse acceptOrderRequest(int idOrder, String qKey, int qValue) {
        return given()
                .queryParam(qKey, qValue)
                .put(BASE_ACCEPT_ORDER_URI + idOrder)
                .then();
    }

    public ValidatableResponse acceptOrderRequest(int idOrder) {
        return given()
                .when()
                .put(BASE_ACCEPT_ORDER_URI + idOrder)
                .then();
    }

}
