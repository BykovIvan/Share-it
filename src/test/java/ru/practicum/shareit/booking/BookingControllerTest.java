package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.StatusOfItem;
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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto = BookingDto.builder()
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
            .build();


    @Test
    void saveNewBookingTest() throws Exception {
        when(bookingService.create(anyLong(), any()))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.owner", is(bookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), StatusOfItem.class));
    }

    @Test
    void updateStatusOfItemInBookingTest() throws Exception {
        when(bookingService.approvedStatusOfItem(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/{id}", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.owner", is(bookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), StatusOfItem.class));
    }

    @Test
    void findBookingByIdOfOwnerOrBookerTest() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/{id}", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.owner", is(bookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$.booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$.bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), StatusOfItem.class));
    }

    @Test
    void findBookingByUserIdAndStateTest() throws Exception {
        List<BookingDto> listOfBookings = new ArrayList<>();
        listOfBookings.add(bookingDto);
        when(bookingService.findBookingByUserIdAndState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfBookings);
        mvc.perform(get("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].owner", is(bookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), StatusOfItem.class));
    }

    @Test
    void findBookingByOwnerIdAndStateTest() throws Exception {
        List<BookingDto> listOfBookings = new ArrayList<>();
        listOfBookings.add(bookingDto);
        when(bookingService.findItemByOwnerIdAndState(anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(listOfBookings);
        mvc.perform(get("/bookings/owner")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id","1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDto.getStart().toString()), String.class))
                .andExpect(jsonPath("$[0].end", is(bookingDto.getEnd().toString()), String.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].itemId", is(bookingDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].owner", is(bookingDto.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].booker", is(bookingDto.getBooker()), UserDto.class))
                .andExpect(jsonPath("$[0].bookerId", is(bookingDto.getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), StatusOfItem.class));
    }


}
