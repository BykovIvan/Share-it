package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTests {

    @Autowired
    private JacksonTester<BookingDto> jsonDto;
    @Autowired
    private JacksonTester<Booking> json;

    @Test
    void testBookingDtoTest() throws Exception {
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 9, 7, 12, 45, 45))
                .end(LocalDateTime.of(2022, 9, 7, 12, 50, 59))
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

        JsonContent<BookingDto> result = jsonDto.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-07T12:45:45");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-09-07T12:50:59");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.ownerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("ivan@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John2");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john2.doe2@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testBookingTest() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .start(Timestamp.valueOf(LocalDateTime.of(2022, 9, 7, 12, 45, 45)))
                .end(Timestamp.valueOf(LocalDateTime.of(2022, 9, 7, 12, 45, 50)))
                .item(Item.builder()
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
                                .created(LocalDateTime.of(2022, 9, 7, 12, 45, 30))
                                .build())
                        .build())
                .booker(User.builder()
                        .id(1L)
                        .name("Ivan")
                        .email("ivan@mail.ru")
                        .build())
                .status(StatusOfItem.REJECTED)
                .build();

        JsonContent<Booking> result = json.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
//        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-09-07T12:45:45.000+00:00");
//        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-09-07T12:45:50.000+00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Hammer");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Hammer for you");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.owner.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.owner.name").isEqualTo("Ivan2");
        assertThat(result).extractingJsonPathStringValue("$.item.owner.email").isEqualTo("ivan2@mail.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.request.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.request.description").isEqualTo("Need for me");
        assertThat(result).extractingJsonPathNumberValue("$.item.request.requestor.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.item.request.requestor.name").isEqualTo("Ivan3");
        assertThat(result).extractingJsonPathStringValue("$.item.request.requestor.email").isEqualTo("ivan3@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.item.request.created").isEqualTo("2022-09-07T12:45:30");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Ivan");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("ivan@mail.ru");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("REJECTED");
    }


}
