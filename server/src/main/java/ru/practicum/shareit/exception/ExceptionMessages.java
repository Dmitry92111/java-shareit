package ru.practicum.shareit.exception;

public class ExceptionMessages {
    private ExceptionMessages() {
    }

    public static final String ENTITY_BY_ID_NOT_FOUND = "Не удалось найти %s с id = %d";
    public static final String FIELD_CANNOT_BE_NULL = "Поле + %s не было заполнено";
    public static final String EMAIL_ALREADY_EXIST = "Пользователь c E-mail: %s уже существует";
    public static final String USER_DONT_HAVE_PERMISSION_TO_MANAGE_ITEM = "Ошибка, вы не являетесь владельцем вещи";
    public static final String USER_DONT_HAVE_PERMISSION_TO_GET_ACCESS_TO_BOOKING = "Вы не имеете права доступа к запрошенному бронированию";
    public static final String USER_DONT_HAVE_PERMISSION_TO_APPROVE_OR_REJECT_BOOKING = "Только владелец вещи может одобрить/отклонить бронирование";
    public static final String BOOKING_STATUS_HAS_ALREADY_BEEN_DECIDED = "Статус бронирования уже был изменен";
    public static final String ITEM_IS_NOT_AVAILABLE_FOR_BOOKING = "Запрошенная вещь недоступна для бронирования";
    public static final String OWNER_CANNOT_BOOK_HIS_OWN_ITEM = "Похоже вы пытаетесь забронировать собственную вещь";
    public static final String USER_HAS_NOT_RENTED_THIS_ITEM_YET = "Похоже вы пытаетесь оставить отзыв вещи, которую не арендовали";

}
