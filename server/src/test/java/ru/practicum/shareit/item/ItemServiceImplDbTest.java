package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.test.DbIntegrationTestBase;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ItemServiceImplDbTest extends DbIntegrationTestBase {

    @Autowired private ItemService itemService;
    @Autowired private UserStorage userStorage;
    @Autowired private ItemStorage itemStorage;
    @Autowired private BookingRepository bookingRepository;

    @Test
    void getOwnerItems_shouldReturnItems_withLastAndNextBookingsForOwner() {
        User owner = userStorage.save(User.builder().name("Owner").email("o@mail.com").build());
        User booker = userStorage.save(User.builder().name("Booker").email("b@mail.com").build());

        Item item = itemStorage.save(Item.builder()
                .name("Bike")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        LocalDateTime now = LocalDateTime.now();

        // last booking (past, approved)
        bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.minusDays(5))
                .end(now.minusDays(3))
                .status(BookingStatus.APPROVED)
                .build());

        // next booking (future, approved)
        Booking next = bookingRepository.save(Booking.builder()
                .item(item)
                .booker(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(5))
                .status(BookingStatus.APPROVED)
                .build());

        List<ItemWithBookingsDto> result = itemService.getOwnerItems(owner.getId());

        assertThat(result).hasSize(1);
        ItemWithBookingsDto dto = result.getFirst();

        assertThat(dto.getId()).isEqualTo(item.getId());
        assertThat(dto.getLastBooking()).isNotNull();
        assertThat(dto.getNextBooking()).isNotNull();
        assertThat(dto.getNextBooking().getId()).isEqualTo(next.getId());
    }
}
