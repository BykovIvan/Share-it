package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemJsonTests {

    @Autowired
    private JacksonTester<ItemDto> jsonDto;
    @Autowired
    private JacksonTester<ItemDtoForRequest> jsonForRequestDto;
    @Autowired
    private JacksonTester<ItemDtoWithComments> jsonWithCommentsDto;
    @Autowired
    private JacksonTester<Item> json;

    @Test
    void testItemDtoTest() throws Exception {

         ItemDto itemDto = ItemDto.builder()
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

        JsonContent<ItemDto> result = jsonDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("ivan@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemDtoForRequestTest() throws Exception {

        ItemDtoForRequest itemDtoForRequest = ItemDtoForRequest.builder()
                .id(1L)
                .name("Test")
                .description("Test for test")
                .available(true)
                .requestId(1L)
                .build();

        JsonContent<ItemDtoForRequest> result = jsonForRequestDto.write(itemDtoForRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Test");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Test for test");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testItemDtoWithCommentsTest() throws Exception {
        List<CommentDto> listOfComments = new ArrayList<>();
        CommentDto comment = CommentDto.builder()
                .id(1L)
                .authorName("Ivan")
                .text("Hello man")
                .created(LocalDateTime.of(2022, 9,7,12,50,59))
                .build();
        listOfComments.add(comment);


        ItemDtoWithComments itemDtoWithComments = ItemDtoWithComments.builder()
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

        JsonContent<ItemDtoWithComments> result = jsonWithCommentsDto.write(itemDtoWithComments);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("john.doe@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.available").isEqualTo("john.doe@mail.com");

        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2022-09-07T12:45:45");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2022-09-07T12:50:59");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.lastBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.owner.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.item.owner.email").isEqualTo("ivan@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.name").isEqualTo("John2");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.booker.email").isEqualTo("john2.doe2@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2022-09-07T12:45:45");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2022-09-07T12:50:59");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.nextBooking.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.owner.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.item.owner.email").isEqualTo("ivan@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.name").isEqualTo("John2");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.booker.email").isEqualTo("john2.doe2@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.nextBooking.status").isEqualTo("WAITING");

        assertThat(result).extractingJsonPathStringValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2022-09-07T12:50:59");
        

    }

    @Test
    void testItemTest() throws Exception {
        Item item = Item.builder()
                .id(1L)
                .name("Hammer")
                .description("Hammer for you")
                .available(true)
                .owner(User.builder()
                        .id(2L)
                        .name("Ivan2")
                        .email("ivan2@mail.ru")
                        .build())
                .request(ItemRequest.builder()
                        .id(1L)
                        .description("Need for me")
                        .requestor(User.builder()
                                .id(3L)
                                .name("Ivan3")
                                .email("ivan3@mail.ru")
                                .build())
                        .created(LocalDateTime.of(2022, 9,7,12,45,30))
                        .build())
                .build();

        JsonContent<Item> result = json.write(item);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("Ivan2");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("ivan2@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.request.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.request.description").isEqualTo("Need for me");
        assertThat(result).extractingJsonPathNumberValue("$.request.requestor.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.request.requestor.name").isEqualTo("Ivan3");
        assertThat(result).extractingJsonPathStringValue("$.request.requestor.email").isEqualTo("ivan3@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.request.created").isEqualTo("2022-09-07T12:45:30");
    }
}
