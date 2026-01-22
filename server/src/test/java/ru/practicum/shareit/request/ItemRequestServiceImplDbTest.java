package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.test.DbIntegrationTestBase;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import static org.assertj.core.api.Assertions.*;

class ItemRequestServiceImplDbTest extends DbIntegrationTestBase {

    @Autowired private ItemRequestService requestService;
    @Autowired private UserStorage userStorage;
    @Autowired private ItemRequestRepository requestRepository;
    @Autowired private ItemStorage itemStorage;

    @Test
    void getById_shouldReturnRequest_withItems() {
        User requestor = userStorage.save(User.builder().name("Req").email("r@mail.com").build());
        User owner = userStorage.save(User.builder().name("Owner").email("o2@mail.com").build());

        // создаём request через сервис (то, что требует ТЗ)
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Need drill");
        ItemRequestDto created = requestService.create(requestor.getId(), createDto);

        // подцепим item к request (через хранилище)
        ItemRequest reqEntity = requestRepository.findById(created.getId()).orElseThrow();
        itemStorage.save(Item.builder()
                .name("Drill")
                .description("desc")
                .available(true)
                .owner(owner)
                .request(reqEntity)
                .build());

        ItemRequestDto got = requestService.getById(requestor.getId(), created.getId());

        assertThat(got.getId()).isEqualTo(created.getId());
        assertThat(got.getItems()).hasSize(1);
        assertThat(got.getItems().getFirst().getName()).isEqualTo("Drill");
    }
}
