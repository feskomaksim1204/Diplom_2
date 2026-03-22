package tests;

import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import model.UserGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserCreationTest {

    private UserClient userClient;
    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешного создания нового уникального пользователя")
    public void testCreateUniqueUser() {
        user = UserGenerator.getRandomUser();

        accessToken = userClient.create(user)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже существует")
    @Description("Попытка создать пользователя с уже зарегистрированными данными")
    public void testCreateDuplicateUser() {
        user = UserGenerator.getRandomUser();

        // Создаём пользователя первый раз
        userClient.create(user);

        // Пытаемся создать с теми же данными
        userClient.create(user)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при регистрации без email")
    public void testCreateUserWithoutEmail() {
        user = UserGenerator.getUserWithoutEmail();

        userClient.create(user)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Проверка ошибки при регистрации без пароля")
    public void testCreateUserWithoutPassword() {
        user = UserGenerator.getUserWithoutPassword();

        userClient.create(user)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Проверка ошибки при регистрации без имени")
    public void testCreateUserWithoutName() {
        user = UserGenerator.getUserWithoutName();

        userClient.create(user)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
