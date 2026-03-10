package tests;

import client.UserClient;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import model.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserTest {

    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        userClient = new UserClient();
        user = UserGenerator.getRandomUser();
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        userClient.create(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание пользователя, который уже существует")
    public void testCreateDuplicateUser() {
        // Создаём пользователя первый раз
        userClient.create(user);

        // Пытаемся создать с теми же данными
        userClient.create(user)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void testCreateUserWithoutEmail() {
        User userWithoutEmail = UserGenerator.getUserWithoutEmail();

        userClient.create(userWithoutEmail)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    public void testCreateUserWithoutPassword() {
        User userWithoutPassword = UserGenerator.getUserWithoutPassword();

        userClient.create(userWithoutPassword)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без name")
    public void testCreateUserWithoutName() {
        User userWithoutName = UserGenerator.getUserWithoutName();

        userClient.create(userWithoutName)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void testLoginExistingUser() {
        // Сначала создаём пользователя
        userClient.create(user);

        // Логинимся
        userClient.login(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void testLoginWithInvalidPassword() {
        // Создаём пользователя
        userClient.create(user);

        // Меняем пароль на неправильный
        User userWithInvalidPassword = new User(user.getEmail(), "wrongpassword");

        userClient.login(userWithInvalidPassword)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным email")
    public void testLoginWithInvalidEmail() {
        // Создаём пользователя
        userClient.create(user);

        // Меняем email на неправильный
        User userWithInvalidEmail = new User("wrong@email.ru", user.getPassword());

        userClient.login(userWithInvalidEmail)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
