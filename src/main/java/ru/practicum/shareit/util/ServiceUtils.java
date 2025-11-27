package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.type.ConditionsNotMetException;

import static ru.practicum.shareit.exception.ExceptionMessages.*;

@Slf4j
public class ServiceUtils {
    private ServiceUtils() {
    }

    public static <T> void checkNotNull(T obj, String fieldName) {
        if (obj == null) {
            log.error("method got null instead of {}", fieldName);
            throw new ConditionsNotMetException(String.format(FIELD_CANNOT_BE_NULL, fieldName));
        }
    }
}
