package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import model.IngredientsList;
import model.User;
import model.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderTest {

    private OrderClient orderClient;
    private UserClient userClient;
    private User user;
    private String accessToken;
    private List<String> validIngredients;
    private List<String> invalidIngredients;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
        user = UserGenerator.getRandomUser();

        // Реальные хеши ингредиентов из документации
        validIngredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        invalidIngredients = Arrays.asList("invalid_hash_123", "invalid_hash_456");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        // Создаём пользователя и получаем токен
        accessToken = userClient.create(user)
                .then()
                .extract()
                .path("accessToken");

        IngredientsList ingredientsList = new IngredientsList(validIngredients);

        orderClient.createWithAuth(ingredientsList, accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void testCreateOrderWithoutAuthWithIngredients() {
        IngredientsList ingredientsList = new IngredientsList(validIngredients);

        orderClient.createWithoutAuth(ingredientsList)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        IngredientsList emptyIngredients = new IngredientsList(Arrays.asList());

        orderClient.createWithoutAuth(emptyIngredients)
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredients() {
        IngredientsList ingredientsList = new IngredientsList(invalidIngredients);

        orderClient.createWithoutAuth(ingredientsList)
                .then()
                .statusCode(500);
    }
}
