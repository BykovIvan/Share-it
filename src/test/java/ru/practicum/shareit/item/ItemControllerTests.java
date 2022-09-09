package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDto test = new ItemDto(1L, "Test", "testtest", true, null, 1L, 1L);

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Hammer")
            .description("Hammer for you")
            .available(true)
//            .ownerId(1L)
            .owner(UserDto.builder()
                    .id(1L)
                    .name("Ivan")
                    .email("ivan@yandex.ru")
                    .build())
//            .requestId(1L)
            .build();


    @Test
    void saveNewItem() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(test);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(test))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(test.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(test.getName())))
                .andExpect(jsonPath("$.description", is(test.getDescription())))
                .andExpect(jsonPath("$.available", is(test.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(test.getOwnerId())))
                .andExpect(jsonPath("$.owner", is(test.getOwner())))
                .andExpect(jsonPath("$.requestId", is(test.getRequestId())));
    }


}
