package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerMockMvcTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean BookingService bookingService;

    @Test
    void create_shouldReturn200_andBody() throws Exception {
        BookingCreateRequestDto req = new BookingCreateRequestDto();
        req.setItemId(1L);
        req.setStart(LocalDateTime.now().plusDays(1));
        req.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponseDto resp = BookingResponseDto.builder()
                .id(10L)
                .start(req.getStart())
                .end(req.getEnd())
                .status(BookingStatus.WAITING)
                .item(BookingResponseDto.ItemShortDto.builder().id(1L).name("X").build())
                .booker(BookingResponseDto.UserShortDto.builder().id(2L).build())
                .build();

        when(bookingService.create(eq(2L), any())).thenReturn(resp);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    void getByBooker_shouldReturnList() throws Exception {
        when(bookingService.getByBooker(1L,"ALL")).thenReturn(List.of());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
