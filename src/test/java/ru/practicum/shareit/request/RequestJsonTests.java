package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestJsonTests {
    @Autowired
    private JacksonTester<ItemRequestDto> jsonDto;
    @Autowired
    private JacksonTester<ItemRequest> json;

    @Test
    void testItemRequestDtoTest() throws Exception {

        ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder()
                .id(1L)
                .name("Test")
                .description("Test for test")
                .available(true)
                .requestId(1L)
                .build();
        List<ItemDtoForRequest> listOfItem = new ArrayList<>();
        listOfItem.add(itemDtoForRequest);

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test for test")
                .created(LocalDateTime.of(2022, 9, 9, 10, 12,23))
                .items(listOfItem)
                .build();

        JsonContent<ItemRequestDto> result = jsonDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test for test");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-09-09T10:12:23");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Test for test");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestId").isEqualTo(1);
    }

    @Test
    void testItemRequestTest() throws Exception {

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test for test")
                .requestor(User.builder()
                        .id(1L)
                        .name("Ivan")
                        .email("ivan@mail.ru")
                        .build())
                .created(LocalDateTime.of(2022, 9, 9, 10, 12,23))
                .build();

        JsonContent<ItemRequest> result = json.write(itemRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test for test");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo("ivan@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2022-09-09T10:12:23");
    }
}
