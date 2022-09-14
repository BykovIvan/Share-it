package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.ItemRequestDto;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTests {

    private final EntityManager em;
    private final ItemService service;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;


    @Test
    void saveItemTest() {
        Exception exception = assertThrows(NoUserInHeaderException.class, () -> {
            service.create(null, new ItemDto());
        });
        String expectedMessage = "В запросе отсутсвует пользователь при создании задачи!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.create(1L, new ItemDto());
        });
        String expectedMessage2 = "Такого пользователя не существует!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);

        service.create(getUser.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));

        ItemDto itemDto2 = makeItemDtoWithRequest("Hammer", 1L, "Hammer for test", true);

        Exception exception3 = assertThrows(NotFoundException.class, () -> {
            service.create(getUser.getId(), itemDto2);
        });
        String expectedMessage3 = "Такого запроса не существует!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        ItemRequestDto itemRequestDto = makeItemRequestDto("Help with hammer");
        ItemRequestDto itemRequestDtoGet = itemRequestService.create(getUser2.getId(), itemRequestDto);

        ItemDto itemDtoForRequest = makeItemDtoWithRequest("Hammer", itemRequestDtoGet.getId(), "Hammer for you", true);

        ItemDto itemDtoForRequestGet = service.create(getUser.getId(), itemDtoForRequest);

        TypedQuery<Item> query3 = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item3 = query3.setParameter("id", itemDtoForRequestGet.getId()).getSingleResult();

        assertThat(item3.getId(), notNullValue());
        assertThat(item3.getName(), equalTo(itemDtoForRequest.getName()));
        assertThat(item3.getDescription(), equalTo(itemDtoForRequest.getDescription()));
        assertThat(item3.getAvailable(), equalTo(itemDtoForRequest.getAvailable()));
    }

    @Test
    void updateItemTest() {
        Exception exception = assertThrows(NoUserInHeaderException.class, () -> {
            service.update(null, 1L, new ItemDto());
        });
        String expectedMessage = "В запросе отсутсвует пользователь при создании задачи!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.update(1L, 1L, new ItemDto());
        });
        String expectedMessage2 = "Такого пользователя не существует!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        Exception exception3 = assertThrows(NotFoundException.class, () -> {
            service.update(getUser.getId(), 1L, new ItemDto());
        });
        String expectedMessage3 = "Такой вещи не существует!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        Exception exception4 = assertThrows(NotFoundException.class, () -> {
            service.update(getUser2.getId(), itemDtoGet.getId(), new ItemDto());
        });
        String expectedMessage4 = "Пользователь не является владельцем данной вещи!";
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage4));

        ItemDto itemDtoUpdate = makeItemDto("Hamyak", "Hamyak for test", true);
        service.update(getUser.getId(), itemDtoGet.getId(), itemDtoUpdate);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoGet.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoUpdate.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoUpdate.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoUpdate.getAvailable()));

    }

    @Test
    void findByItemIdTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.findById(1L);
        });
        String expectedMessage = "Такой вещи не найдено";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        Item itemDtoById = service.findById(itemDtoGet.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoGet.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoById.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoById.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoById.getAvailable()));

    }

    @Test
    void findByUserIdAndItemIdAllTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        Item itemDtoById = service.findByUserIdAndItemIdAll(getUser.getId(), itemDtoGet.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoGet.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoById.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoById.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDtoById.getAvailable()));

    }


    @Test
    void containsByIdItemTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);
        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        boolean isExists = service.containsById(itemDtoGet.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDtoGet.getId()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat("true", isExists);

    }

    @Test
    void saveCommentTest() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.addCommentToItem(1L, 1L, new CommentDto());
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.addCommentToItem(getUser2.getId(), 1L, new CommentDto());
        });
        String expectedMessage2 = "Такой вещи не существует!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        ItemDto itemDto2 = makeItemDto("Hammer2", "Hammer2 for test", true);
        ItemDto itemDtoGet2 = service.create(getUser.getId(), itemDto);

        CommentDto commentDtoWithOutCom = makeCommentDtoWithOutCom(userDto2.getName());

        Exception exception3 = assertThrows(BadRequestException.class, () -> {
            service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDtoWithOutCom);
        });
        String expectedMessage3 = "Комментарий отсутсвует!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        CommentDto commentDto = makeCommentDto("Hello text", userDto2.getName());

        Exception exception4 = assertThrows(NotFoundException.class, () -> {
            service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDto);
        });
        String expectedMessage4 = "Бронирование данной вещи не существует!";
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage4));

        BookingDto bookingDtoWithFutureEndTime = makeBookingDtoWithFutureEndTime(itemDtoGet2, getUser);
        bookingService.create(getUser2.getId(), bookingDtoWithFutureEndTime);

        Exception exception5 = assertThrows(BadRequestException.class, () -> {
            service.addCommentToItem(getUser2.getId(), itemDtoGet2.getId(), commentDto);
        });
        String expectedMessage5 = "Пока ни одного бронирования не завершено!";
        String actualMessage5 = exception5.getMessage();
        assertTrue(actualMessage5.contains(expectedMessage5));

        BookingDto bookingDto = makeBookingDto(itemDtoGet, getUser);
        bookingService.create(getUser2.getId(), bookingDto);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDto);

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment comment = query.setParameter("text", commentDto.getText()).getSingleResult();
        CommentDto commentDtoFromSql = CommentMapping.toCommentDto(comment);

        assertThat(commentDtoFromSql.getId(), notNullValue());
        assertThat(commentDtoFromSql.getText(), equalTo(commentDto.getText()));
        assertThat(commentDtoFromSql.getAuthorName(), equalTo(commentDto.getAuthorName()));

    }

    @Test
    void getCommentByIdTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(itemDtoGet, getUser);
        bookingService.create(getUser2.getId(), bookingDto);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CommentDto commentDto = makeCommentDto("Hello text", userDto2.getName());
        CommentDto commentDtoGet = service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDto);

        List<Comment> commentDtoGetById = service.getCommentByIdItem(itemDtoGet.getId());

        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.text = :text", Comment.class);
        List<Comment> comments = query.setParameter("text", commentDto.getText()).getResultList();

        assertThat(comments.get(0).getId(), notNullValue());
        assertThat(comments.get(0).getText(), equalTo(commentDtoGetById.get(0).getText()));
        assertThat(comments.get(0).getAuthor().getName(), equalTo(commentDtoGetById.get(0).getAuthor().getName()));
        assertThat(comments.get(0).getAuthor().getEmail(), equalTo(commentDtoGetById.get(0).getAuthor().getEmail()));

    }

    @Test
    void getByUserIdAndItemIdTest() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.findByUserIdAndItemId(1L, 1L);
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.findByUserIdAndItemId(getUser.getId(), 1L);
        });
        String expectedMessage2 = "Такой вещи не существует!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(itemDtoGet, getUser);
        bookingService.create(getUser2.getId(), bookingDto);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CommentDto commentDto = makeCommentDto("Hello text", userDto2.getName());
        service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDto);

        ItemDtoWithComments itemDtoWithComments = service.findByUserIdAndItemId(getUser.getId(), itemDtoGet.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i join User u on i.owner.id = u.id where u.id = :ownerId and i.id = :id ", Item.class);
        Item item = query.setParameter("ownerId", getUser.getId()).setParameter("id", itemDtoGet.getId()).getSingleResult();


        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoWithComments.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoWithComments.getDescription()));

    }

    private ItemRequestDto makeItemRequestDto(String description) {
        ItemRequestDto dto = ItemRequestDto.builder()
                .description(description)
                .created(LocalDateTime.now())
                .build();
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private ItemDto makeItemDtoWithRequest(String name, Long requestId, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setRequestId(requestId);
        dto.setAvailable(available);
        return dto;
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }

    private CommentDto makeCommentDto(String text, String authorName) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(text);
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }

    private CommentDto makeCommentDtoWithOutCom(String authorName) {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorName(authorName);
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }

    private BookingDto makeBookingDto(ItemDto itemDto, UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(StatusOfItem.WAITING);
        return bookingDto;
    }

    private BookingDto makeBookingDtoWithFutureEndTime(ItemDto itemDto, UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(200));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(StatusOfItem.WAITING);
        return bookingDto;
    }

}
