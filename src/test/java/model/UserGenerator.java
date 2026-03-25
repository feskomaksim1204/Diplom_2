package model;

import java.util.Random;

public class UserGenerator {
    private static final Random random = new Random();

    public static User getRandomUser() {
        String email = "test" + random.nextInt(999999) + "@yandex.ru";
        String password = "password" + random.nextInt(999999);
        String name = "User" + random.nextInt(999999);
        return new User(email, password, name);
    }

    public static User getUserWithoutEmail() {
        String password = "password" + random.nextInt(999999);
        String name = "User" + random.nextInt(999999);
        return new User(null, password, name);
    }

    public static User getUserWithoutPassword() {
        String email = "test" + random.nextInt(999999) + "@yandex.ru";
        String name = "User" + random.nextInt(999999);
        return new User(email, null, name);
    }

    public static User getUserWithoutName() {
        String email = "test" + random.nextInt(999999) + "@yandex.ru";
        String password = "password" + random.nextInt(999999);
        return new User(email, password, null);
    }

    public static User getUserWithInvalidPassword() {
        String email = "test" + random.nextInt(999999) + "@yandex.ru";
        String password = "123"; // меньше 6 символов
        String name = "User" + random.nextInt(999999);
        return new User(email, password, name);
    }
}
