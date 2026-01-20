package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item findById(Long id);

    List<Item> findAllByOwnerId(Long userId);

    Item create(Item item, Long ownerId);

    Item update(Long itemId, Long userId, Item item);

    void deleteById(Long itemId, Long userId);

    List<Item> search(String text);
}
