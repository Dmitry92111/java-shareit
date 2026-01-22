package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.type.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ServiceUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ExceptionMessages.*;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemServiceImpl(ItemStorage itemStorage,
                           UserService userService,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
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
    @Transactional
    public Item create(Item item, Long ownerId, Long requestId) {
        log.debug("Trying to create Item {}", item);
        item.setOwner(userService.findById(ownerId));
        if (requestId != null) {
            ItemRequest req = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format(ENTITY_BY_ID_NOT_FOUND, "request", requestId)));
            item.setRequest(req);
        }
        item = itemStorage.save(item);
        log.info("Item has been created successfully with id {}", item.getId());
        return item;
    }

    @Override
    @Transactional
    public Item update(Long itemId, Long userId, Item item) {
        log.debug("User with id {} is trying to update Item {}", userId, item);
        Item existingItem = findById(itemId);
        checkUserPermissionToManageItem(userId, existingItem.getOwner().getId());
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }
        item = itemStorage.update(existingItem);
        log.info("Item with id {} has been updated successfully", item.getId());
        return item;
    }

    @Override
    @Transactional
    public void deleteById(Long itemId, Long userId) {
        log.debug("Trying to delete Item with id {}", itemId);
        Item item = findById(itemId);
        checkUserPermissionToManageItem(userId, item.getOwner().getId());
        itemStorage.deleteById(itemId);
        log.info("Item with id {} has been deleted successfully", itemId);
    }

    @Override
    public List<Item> search(String text) {
        log.debug("Trying to search Items by text {}", text);
        if (text == null || text.isBlank()) {
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

    @Override
    public List<ItemWithBookingsDto> getOwnerItems(Long ownerId) {
        log.debug("Trying get items of owner with id = {}", ownerId);
        List<Item> items = itemStorage.findAllByOwnerId(ownerId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .toList();

        Map<Long, List<CommentDto>> commentsByItemId = commentRepository
                .findAllByItemIdInOrderByCreatedDesc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(
                        c -> c.getItem().getId(),
                        Collectors.mapping(ItemMapper::toCommentDto, Collectors.toList())
                ));

        LocalDateTime now = LocalDateTime.now();

        Map<Long, Booking> lastByItemId = bookingRepository
                .findLastApprovedBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        Function.identity(),
                        (existing, ignored) -> existing
                ));

        Map<Long, Booking> nextByItemId = bookingRepository
                .findNextApprovedBookingsForItems(itemIds, now)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        Function.identity(),
                        (existing, ignored) -> existing
                ));


        return items.stream()
                .map(item -> ItemMapper.toItemWithBookingsDto(
                        item,
                        lastByItemId.get(item.getId()),
                        nextByItemId.get(item.getId()),
                        commentsByItemId.getOrDefault(item.getId(), List.of())
                ))
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        User author = userService.findById(userId);

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "item", itemId)));

        LocalDateTime now = LocalDateTime.now();

        boolean hasFinishedApprovedBooking = bookingRepository
                .existsByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId,
                        userId,
                        BookingStatus.APPROVED,
                        now
                );

        if (!hasFinishedApprovedBooking) {
            throw new ConditionsNotMetException(USER_HAS_NOT_RENTED_THIS_ITEM_YET);
        }

        Comment comment = Comment.builder()
                .text(dto.getText())
                .item(item)
                .author(author)
                .created(now)
                .build();

        Comment saved = commentRepository.save(comment);

        return CommentDto.builder()
                .id(saved.getId())
                .text(saved.getText())
                .authorName(saved.getAuthor().getName())
                .created(saved.getCreated())
                .build();
    }

    @Override
    public ItemWithBookingsDto getItemByIdWithBookings(Long userId, Long itemId) {

        userService.findById(userId);
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ENTITY_BY_ID_NOT_FOUND, "item", itemId)));

        boolean isOwner = item.getOwner().getId().equals(userId);

        LocalDateTime now = LocalDateTime.now();

        Booking last = null;
        Booking next = null;

        if (isOwner) {
            last = bookingRepository
                    .findLastApprovedBookingsForItems(List.of(itemId), now)
                    .stream()
                    .findFirst()
                    .orElse(null);

            next = bookingRepository
                    .findNextApprovedBookingsForItems(List.of(itemId), now)
                    .stream()
                    .findFirst()
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository
                .findAllByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(ItemMapper::toCommentDto)
                .toList();

        return ItemWithBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .lastBooking(ItemMapper.toShortBooking(last))
                .nextBooking(ItemMapper.toShortBooking(next))
                .comments(comments)
                .build();
    }
}
