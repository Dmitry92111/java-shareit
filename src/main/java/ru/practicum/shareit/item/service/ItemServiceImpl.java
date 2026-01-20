package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.type.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ServiceUtils;

import java.util.List;

import static ru.practicum.shareit.exception.ExceptionMessages.*;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    ItemStorage itemStorage;
    UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item findById(Long id) {
        log.debug("Trying to find Item by id {}", id);
        ServiceUtils.checkNotNull(id, "id");
        return itemStorage.findById(id)
                .map(item -> {
                    log.info("Item with id {} has been found successfully", id);
                    return item;
                })
                .orElseThrow(() -> {
                    log.error("Item with id {} not found", id);
                    return new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "item", id));
                });
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        log.debug("Trying to find all Items of user with id {}", ownerId);
        userService.findById(ownerId);
        List<Item> items = itemStorage.findAllByOwnerId(ownerId);
        log.info("Items of user with id {} has been found successfully", ownerId);
        return items;
    }

    @Override
    public Item create(Item item, Long ownerId) {
        log.debug("Trying to create Item {}", item);
        userService.findById(ownerId);
        item.setOwnerId(ownerId);
        item = itemStorage.save(item);
        log.info("Item has been created successfully with id {}", item.getId());
        return item;
    }

    @Override
    public Item update(Long itemId, Long userId, Item item) {
        log.debug("User with id {} is trying to update Item {}", userId, item);
        Item existingItem = findById(itemId);
        checkUserPermissionToManageItem(userId, existingItem.getOwnerId());
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        if (item.getOwnerId() != null && !item.getOwnerId().equals(existingItem.getOwnerId())) {
            log.warn("Some process is trying to change ownerId for Item with id {}, " +
                    "operation shouldn't exist", item.getId());
        }
        item = itemStorage.update(existingItem);
        log.info("Item with id {} has been updated successfully", item.getId());
        return item;
    }

    @Override
    public void deleteById(Long itemId, Long userId) {
        log.debug("Trying to delete Item with id {}", itemId);
        Item item = findById(itemId);
        checkUserPermissionToManageItem(userId, item.getOwnerId());
        itemStorage.deleteById(itemId);
        log.info("Item with id {} has been deleted successfully", itemId);
    }

    @Override
    public List<Item> search(String text) {
        log.debug("Trying to search Items by text {}", text);
        if (text.isBlank()) {
            log.warn("Search query is null, empty or blank");
            return List.of();
        }
        List<Item> items = itemStorage.searchItemsByText(text);
        log.info("Items has been found successfully: {}", items);
        return items;
    }

    private void checkUserPermissionToManageItem(Long userId, Long ownerId) {
        if (!ownerId.equals(userId)) {
            log.warn("User with id {} is not an owner of this item (ownerId = {})", userId, ownerId);
            throw new ForbiddenException(USER_DONT_HAVE_PERMISSION_TO_MANAGE_ITEM);
        }
    }
}
