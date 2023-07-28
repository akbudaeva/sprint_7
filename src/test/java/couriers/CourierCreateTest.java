package couriers;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import service.Service;
public class CourierCreateTest {

    private final CourierAPI courierApi = new CourierAPI();
    private final CourierData courierData = new CourierData();
    private Courier courierRandom;

    @Before
    public void setUp() {
        RestAssured.baseURI = Service.BASE_URL;
        courierRandom = courierData.generateRandom();
    }

    @After
    public void tearDown() {
        try {
            Response responseLogin = courierLogin(courierRandom);
            String courierId = responseLogin.then().extract().path("id").toString();
            courierDelete(courierId);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Test
    @DisplayName("Регистрация нового курьера")
    @Description("Проверка, что можно создать нового курьера с корректыми введенными данными")
    public void createNewCourierPositiveTest() {
        Response response = courierCreate(courierRandom);
        compareResultToTrue(response, SC_CREATED);
    }

    @Test
    @DisplayName("Регистрация нового курьера без логина")
    @Description("Проверка, что невозможно создать нового курьера без указания логина")
    public void createNewCourierNoLoginTest() {
        courierRandom.setLogin("");
        Response response = courierCreate(courierRandom);
        compareResultMessageToText(response, SC_BAD_REQUEST, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Регистрация нового повторяющегося курьера")
    @Description("Проверка, что невозможно создать нового курьера, который уже существует")
    public void createNewDuplicateCourierTest() {
        // От первого ответ не нужен
        courierCreate(courierRandom);
        Response response = courierCreate(courierRandom);
        compareResultMessageToText(response, SC_CONFLICT, "Этот логин уже используется. Попробуйте другой.");
    }

    // Метод для шага "Создать курьера":
    @Step("Create courier")
    public Response courierCreate(Courier courier){
        Response response = courierApi.create(courier);
        printResponseBodyToConsole("Создание курьера: ", response, Service.NEED_DETAIL_LOG);
        return response;
    }

    // Метод для шага "Авторизация курьера":
    @Step("Login courier")
    public Response courierLogin(Courier courier){
        Response response = courierApi.login(courier);
        printResponseBodyToConsole("Авторизация курьера: ", response, Service.NEED_DETAIL_LOG);
        return response;
    }

    // Метод для шага "Удалить курьера":
    @Step("Delete courier by id")
    public void courierDelete(String courierId){
        Response response = courierApi.delete(courierId);
        printResponseBodyToConsole("Удаление курьера: ", response, Service.NEED_DETAIL_LOG);
    }

    @Step("Compare result to true")
    public void compareResultToTrue(Response response, int statusCode){
        response
                .then()
                .assertThat()
                .log().all()
                .statusCode(statusCode)
                .body("ok", is(true));
    }

    @Step("Compare result message to something")
    public void compareResultMessageToText(Response response, int statusCode, String text){
        response
                .then()
                .log().all()
                .statusCode(statusCode)
                .and()
                .assertThat()
                .body("message", is(text));
    }

    // Метод для шага "Вывести тело ответа в консоль":
    @Step("Print response body to console")
    public void printResponseBodyToConsole(String headerText, Response response, boolean detailedLog){
        if (detailedLog)
            System.out.println(headerText + response.body().asString());
    }

}
