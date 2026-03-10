package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.IngredientsList;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    private static final String ORDERS_PATH = "api/orders";

    @Step("Создание заказа с авторизацией")
    public Response createWithAuth(IngredientsList ingredients, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(ingredients)
                .when()
                .post(ORDERS_PATH);
    }

    @Step("Создание заказа без авторизации")
    public Response createWithoutAuth(IngredientsList ingredients) {
        return given()
                .spec(getBaseSpec())
                .body(ingredients)
                .when()
                .post(ORDERS_PATH);
    }
}
