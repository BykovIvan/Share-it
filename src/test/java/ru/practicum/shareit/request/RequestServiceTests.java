package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestDto;
import ru.practicum.shareit.requests.ItemRequestMapping;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserMapping;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTests {

    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void saveItemRequestTest() {
        UserDto userDto = makeUserDto("Ivan", "ivan@yandex.ru");
        UserDto getUser = userService.create(userDto);
        UserMapping.toUser(getUser);

        ItemDtoForRequest item = makeItemDtoForRequest("Hammer", "Test for Hammer", true);

        List<ItemDtoForRequest> list = new ArrayList<>();
        list.add(item);
        ItemRequestDto itemRequestDto = makeItemRequestDto("Test for test", list);

        ItemRequestDto getItemRequest = service.create(getUser.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", getItemRequest.getId()).getSingleResult();
        ItemRequestDto resultItemRequest = ItemRequestMapping.toItemRequestDto(itemRequest);

        assertThat(resultItemRequest.getId(), notNullValue());
        assertThat(resultItemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(resultItemRequest.getCreated().format(formatter), equalTo(itemRequestDto.getCreated().format(formatter)));
    }


    @Test
    void findItemRequestByUserIdTest() {
        UserDto userDto = makeUserDto("Ivan", "ivan@yandex.ru");
        UserDto getUser = userService.create(userDto);

        ItemDtoForRequest item = makeItemDtoForRequest("Hammer", "Test for Hammer", true);

        List<ItemDtoForRequest> list = new ArrayList<>();
        list.add(item);

        ItemRequestDto itemRequestDto = makeItemRequestDto("Test for test", list);
        ItemRequestDto getItemRequest = service.create(getUser.getId(), itemRequestDto);

        List<ItemRequestDto> result = service.findRequestByUserId(getUser.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i join User u on i.requestor.id = u.id where u.id = :id", ItemRequest.class);
        List<ItemRequestDto> itemRequest = query.setParameter("id", getUser.getId()).getResultList().stream()
                .map(ItemRequestMapping::toItemRequestDto)
                .collect(Collectors.toList());

        assertThat(itemRequest.get(0).getId(), notNullValue());
        assertThat(itemRequest.get(0).getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemRequest.get(0).getCreated().format(formatter), equalTo(result.get(0).getCreated().format(formatter)));
    }

    @Test
    void findItemRequestByIdTest() {
        UserDto userDto = makeUserDto("Ivan", "ivan@yandex.ru");
        UserDto getUser = userService.create(userDto);

        ItemDtoForRequest item = makeItemDtoForRequest("Hammer", "Test for Hammer", true);

        List<ItemDtoForRequest> list = new ArrayList<>();
        list.add(item);

        ItemRequestDto itemRequestDto = makeItemRequestDto("Test for test", list);
        ItemRequestDto getItemRequest = service.create(getUser.getId(), itemRequestDto);

        List<ItemRequestDto> result = service.findRequestByUserId(getUser.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        List<ItemRequestDto> itemRequest = query.setParameter("id", getItemRequest.getId()).getResultList().stream()
                .map(ItemRequestMapping::toItemRequestDto)
                .collect(Collectors.toList());

        assertThat(itemRequest.get(0).getId(), notNullValue());
        assertThat(itemRequest.get(0).getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemRequest.get(0).getCreated().format(formatter), equalTo(result.get(0).getCreated().format(formatter)));

    }

    @Test
    void findItemRequestByByParamTest() {
        UserDto userDto = makeUserDto("Ivan", "ivan@yandex.ru");
        UserDto getUser = userService.create(userDto);
        UserDto userDto2 = makeUserDto("Ivan2", "ivan2@yandex.ru");
        UserDto getUser2 = userService.create(userDto2);

        ItemDtoForRequest item = makeItemDtoForRequest("Hammer", "Test for Hammer", true);

        List<ItemDtoForRequest> list = new ArrayList<>();
        list.add(item);

        ItemRequestDto itemRequestDto = makeItemRequestDto("Test for test", list);
        ItemRequestDto getItemRequest = service.create(getUser.getId(), itemRequestDto);

        List<ItemRequestDto> result = service.findRequestByParam(getUser2.getId(), 0, 1);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        List<ItemRequestDto> itemRequest = query.setParameter("id", getItemRequest.getId()).getResultList().stream()
                .map(ItemRequestMapping::toItemRequestDto)
                .collect(Collectors.toList());

        assertThat(itemRequest.get(0).getId(), notNullValue());
        assertThat(itemRequest.get(0).getDescription(), equalTo(result.get(0).getDescription()));
        assertThat(itemRequest.get(0).getCreated().format(formatter), equalTo(result.get(0).getCreated().format(formatter)));

    }

    private ItemRequestDto makeItemRequestDto(String description, List<ItemDtoForRequest> listOfItems) {

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(description);
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setItems(listOfItems);
        return itemRequestDto;
    }

    private ItemDtoForRequest makeItemDtoForRequest(String name, String description, Boolean available) {
        ItemDtoForRequest dto = new ItemDtoForRequest();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(1L);
        return dto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

}
