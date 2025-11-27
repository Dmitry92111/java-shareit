package ru.practicum.shareit.exception;

public class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String ENTITY_BY_ID_NOT_FOUND = "Не удалось найти %s с id = %d";
    public static final String FIELD_CANNOT_BE_NULL = "Поле + %s не было заполнено";
    public static final String EMAIL_ALREADY_EXIST = "Пользователь c E-mail: %s уже существует";
    public static final String USER_DONT_HAVE_PERMISSION_TO_MANAGE_ITEM = "Ошибка, вы не являетесь владельцем вещи";
}
