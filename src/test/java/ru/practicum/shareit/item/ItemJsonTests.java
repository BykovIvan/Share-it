package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

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
    void testItemDtoForRequestTest() throws Exception {


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
    void testItemDtoWithCommentsTest() throws Exception {


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
    void testItemTest() throws Exception {


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
