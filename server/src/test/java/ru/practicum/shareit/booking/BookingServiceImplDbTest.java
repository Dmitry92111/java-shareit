package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.test.DbIntegrationTestBase;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class BookingServiceImplDbTest extends DbIntegrationTestBase {

    @Autowired private BookingService bookingService;
    @Autowired private UserStorage userStorage;
    @Autowired private ItemStorage itemStorage;

    @Test
    void create_thenApprove_shouldWork_andChangeStatus() {

        User owner = userStorage.save(User.builder().name("Owner").email("owner@mail.com").build());
        User booker = userStorage.save(User.builder().name("Booker").email("booker@mail.com").build());

        Item item = itemStorage.save(Item.builder()
                .name("Drill")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(2);

        BookingCreateRequestDto req = new BookingCreateRequestDto();
        req.setItemId(item.getId());
        req.setStart(start);
        req.setEnd(end);

        BookingResponseDto created = bookingService.create(booker.getId(), req);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(created.getItem().getId()).isEqualTo(item.getId());
        assertThat(created.getBooker().getId()).isEqualTo(booker.getId());

        BookingResponseDto approved = bookingService.approve(owner.getId(), created.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
