package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private UserDto userDto = UserDto.builder()
            .id(1L)
            .name("John")
            .email("john.doe@mail.com")
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Hammer")
            .description("Hammer for you")
            .available(true)
            .ownerId(1L)
            .owner(userDto)
            .requestId(1L)
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .start(LocalDateTime.of(2022, 9,7,12,45,45))
            .end(LocalDateTime.of(2022, 9,7,12,50,59))
            .item(itemDto)
            .itemId(itemDto.getId())
            .owner(1L)
            .bookerId(1L)
            .status(StatusOfItem.WAITING)
            .build();

}
