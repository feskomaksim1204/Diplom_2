package tests;

import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.IngredientsList;
import model.User;
import model.UserGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
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

        // Создаём пользователя для тестов
        accessToken = userClient.create(user)
                .then()
                .extract()
                .path("accessToken");

        // Реальные хеши ингредиентов из документации
        validIngredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        invalidIngredients = Arrays.asList("invalid_hash_123", "invalid_hash_456");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Проверка успешного создания заказа авторизованным пользователем с валидными ингредиентами")
    public void testCreateOrderWithAuthAndIngredients() {
        IngredientsList ingredientsList = new IngredientsList(validIngredients);

        orderClient.createWithAuth(ingredientsList, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    @Description("Проверка создания заказа неавторизованным пользователем")
    public void testCreateOrderWithoutAuthWithIngredients() {
        IngredientsList ingredientsList = new IngredientsList(validIngredients);

        orderClient.createWithoutAuth(ingredientsList)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов (авторизованный пользователь)")
    @Description("Проверка ошибки при создании заказа без ингредиентов авторизованным пользователем")
    public void testCreateOrderWithoutIngredients() {
        IngredientsList emptyIngredients = new IngredientsList(Arrays.asList());

        orderClient.createWithAuth(emptyIngredients, accessToken)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов (авторизованный пользователь)")
    @Description("Проверка ошибки при создании заказа с невалидными ингредиентами")
    public void testCreateOrderWithInvalidIngredients() {
        IngredientsList ingredientsList = new IngredientsList(invalidIngredients);

        orderClient.createWithAuth(ingredientsList, accessToken)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
