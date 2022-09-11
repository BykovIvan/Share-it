package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.user.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
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

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Hammer")
            .description("Hammer for you")
            .available(true)
            .ownerId(1L)
            .owner(UserDto.builder()
                    .id(1L)
                    .name("Ivan")
                    .email("ivan@yandex.ru")
                    .build())
            .requestId(1L)
            .build();

    List<CommentDto> listOfComments = new ArrayList<>();
    CommentDto comment = CommentDto.builder()
            .id(1L)
            .authorName("Ivan")
            .text("Hello man")
            .created(LocalDateTime.of(2022, 9,7,12,50,59))
            .build();

    private ItemDtoWithComments itemDtoWithComments = ItemDtoWithComments.builder()
            .id(1L)
            .name("Hammer")
            .description("Hammer for you")
            .available(true)
            .lastBooking(BookingDto.builder()
                    .id(1L)
                    .start(LocalDateTime.of(2022, 9,7,12,45,45))
                    .end(LocalDateTime.of(2022, 9,7,12,50,59))
                    .item(ItemDto.builder()
                            .id(1L)
                            .name("Hammer")
                            .description("Hammer for you")
                            .available(true)
                            .ownerId(1L)
                            .owner(UserDto.builder()
                                    .id(1L)
                                    .name("Ivan")
                                    .email("ivan@yandex.ru")
                                    .build())
                            .requestId(1L)
                            .build())
                    .itemId(1L)
                    .owner(1L)
                    .bookerId(1L)
                    .booker(UserDto.builder()
                            .id(2L)
                            .name("John2")
                            .email("john2.doe2@mail.com")
                            .build())
                    .status(StatusOfItem.WAITING)
                    .build())
            .nextBooking(BookingDto.builder()
                    .id(2L)
                    .start(LocalDateTime.of(2022, 10,7,12,45,45))
                    .end(LocalDateTime.of(2022, 10,7,12,50,59))
                    .item(ItemDto.builder()
                            .id(2L)
                            .name("Hammer2")
                            .description("Hammer2 for you")
                            .available(true)
                            .ownerId(1L)
                            .owner(UserDto.builder()
                                    .id(1L)
                                    .name("Ivan2")
                                    .email("ivan2@yandex.ru")
                                    .build())
                            .requestId(1L)
                            .build())
                    .itemId(1L)
                    .owner(1L)
                    .bookerId(1L)
                    .booker(UserDto.builder()
                            .id(2L)
                            .name("John3")
                            .email("john3.doe2@mail.com")
                            .build())
                    .status(StatusOfItem.WAITING)
                    .build())
            .comments(listOfComments)
            .build();

    @Test
    void saveNewItemTest() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), UserDto.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void updateNewItemTest() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{id}", "1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.owner", is(itemDto.getOwner()), UserDto.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void findAllItemsTest() throws Exception {
        listOfComments.add(comment);
        List<ItemDtoWithComments> listOfItems = new ArrayList<>();
        listOfItems.add(itemDtoWithComments);

        when(itemService.findAllItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfItems);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoWithComments.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoWithComments.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoWithComments.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoWithComments.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemDtoWithComments.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemDtoWithComments.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemDtoWithComments.getComments().get(0).getId()), Long.class));
    }


    @Test
    void findByUserIdAndItemIdTest() throws Exception {
        listOfComments.add(comment);
        when(itemService.findByUserIdAndItemId(anyLong(), anyLong()))
                .thenReturn(itemDtoWithComments);

        mvc.perform(get("/items/{id}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithComments.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithComments.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithComments.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithComments.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoWithComments.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemDtoWithComments.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(itemDtoWithComments.getComments().get(0).getId()), Long.class));
    }

    @Test
    void findItemsByTextTest() throws Exception {
        listOfComments.add(comment);
        List<ItemDto> listOfItems = new ArrayList<>();
        listOfItems.add(itemDto);

        when(itemService.findByText(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(listOfItems);

        mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .param("text", "Bla")
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDto.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].owner", is(itemDto.getOwner()), UserDto.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDto.getRequestId()), Long.class));
    }

    @Test
    void saveNewCommentTest() throws Exception {
        when(itemService.addCommentToItem(anyLong(), anyLong(), any()))
                .thenReturn(comment);

        mvc.perform(post("/items/{id}/comment", "1")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().toString())));
    }

}
