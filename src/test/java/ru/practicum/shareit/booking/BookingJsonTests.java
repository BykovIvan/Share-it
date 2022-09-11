package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingJsonTests {

    @Autowired
    private JacksonTester<BookingDto> jsonDto;
    @Autowired
    private JacksonTester<Booking> json;

    @Test
    void testBookingDtoTest() throws Exception {

//        UserDto userDto = UserDto.builder()
//                .id(1L)
//                .name("John")
//                .email("john.doe@mail.com")
//                .build();
//
//        JsonContent<UserDto> result = jsonDto.write(userDto);
//
//        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
//        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
//        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

    @Test
    void testBookingTest() throws Exception {


//        User user = User.builder()
//                .id(1L)
//                .name("John")
//                .email("john.doe@mail.com")
//                .build();
//
//        JsonContent<User> result = json.write(user);
//
//        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
//        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John");
//        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john.doe@mail.com");
    }

}
