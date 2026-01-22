package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void serialize_shouldContainNestedFields_andDates() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 1, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 2, 12, 0);

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(10L)
                .start(start)
                .end(end)
                .status(BookingStatus.APPROVED)
                .item(BookingResponseDto.ItemShortDto.builder().id(1L).name("Drill").build())
                .booker(BookingResponseDto.UserShortDto.builder().id(2L).build())
                .build();

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(content).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.item.name").isEqualTo("Drill");
        assertThat(content).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);

        // LocalDateTime по умолчанию сериализуется в строку ISO-формата
        assertThat(content).extractingJsonPathStringValue("$.start").contains("2030-01-01T12:00");
        assertThat(content).extractingJsonPathStringValue("$.end").contains("2030-01-02T12:00");
    }
}
