package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exception.type.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.exception.ExceptionMessages.ENTITY_BY_ID_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository; // JPA repo (не ItemStorage)
    private final UserStorage userStorage;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", userId)));

        ItemRequest req = ItemRequest.builder()
                .description(dto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest saved = requestRepository.save(req);

        return ItemRequestDto.builder()
                .id(saved.getId())
                .description(saved.getDescription())
                .created(saved.getCreated())
                .items(List.of())
                .build();
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        ensureUserExists(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return enrichWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllOthers(Long userId) {
        ensureUserExists(userId);

        List<ItemRequest> requests = requestRepository.findAllOthers(userId);
        return enrichWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        ensureUserExists(userId);

        ItemRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "request", requestId)));

        List<ItemForRequestDto> items = itemRepository.findAllByRequestId(requestId).stream()
                .map(this::toItemForRequestDto)
                .toList();

        return ItemRequestDto.builder()
                .id(req.getId())
                .description(req.getDescription())
                .created(req.getCreated())
                .items(items)
                .build();
    }

    private void ensureUserExists(Long userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(ENTITY_BY_ID_NOT_FOUND, "user", userId)));
    }

    private List<ItemRequestDto> enrichWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> requestIds = requests.stream().map(ItemRequest::getId).toList();

        Map<Long, List<ItemForRequestDto>> itemsByRequestId = itemRepository
                .findAllByRequestIdIn(requestIds)
                .stream()
                .collect(Collectors.groupingBy(
                        i -> i.getRequest().getId(),
                        Collectors.mapping(this::toItemForRequestDto, Collectors.toList())
                ));

        return requests.stream()
                .map(r -> ItemRequestDto.builder()
                        .id(r.getId())
                        .description(r.getDescription())
                        .created(r.getCreated())
                        .items(itemsByRequestId.getOrDefault(r.getId(), List.of()))
                        .build())
                .toList();
    }

    private ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}

