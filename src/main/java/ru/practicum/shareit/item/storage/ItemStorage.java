package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Optional<Item> findById(Long id);

    List<Item> findAllByOwnerId(Long userId);

    List<Item> searchItemsByText(String text);

    Item save(Item item);

    Item update(Item item);

    void deleteById(Long id);

    boolean existsById(Long id);
}
