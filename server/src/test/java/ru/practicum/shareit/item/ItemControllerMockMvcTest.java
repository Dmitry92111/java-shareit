package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerMockMvcTest {

    @Autowired MockMvc mvc;

    @MockBean ItemService itemService;

    @Test
    void search_shouldReturn200() throws Exception {
        when(itemService.search("bike")).thenReturn(List.of());

        mvc.perform(get("/items/search").param("text", "bike"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getItems_shouldReturn200() throws Exception {
        when(itemService.getOwnerItems(1L)).thenReturn(List.of());

        mvc.perform(get("/items").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
