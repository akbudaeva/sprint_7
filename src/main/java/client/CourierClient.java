package client;

import model.Courier;
import io.restassured.response.ValidatableResponse;
import model.CourierCredentials;

import static base.Config.Urls.BASE_URI;
import static io.restassured.RestAssured.given;


public class CourierClient {
    private static final String COURIER_URI = "api/v1/courier/";


    public ValidatableResponse create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post(COURIER_URI)
                .then();
    }

    public ValidatableResponse login(CourierCredentials courierCredentials) {
        return given()
                .header("Content-type", "application/json")
                .body(courierCredentials)
                .when()
                .post(COURIER_URI + "login/")
                .then();
    }

    public ValidatableResponse delete(int id) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(COURIER_URI + id)
                .then();
    }
    public ValidatableResponse delete() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(COURIER_URI)
                .then();
    }
}
