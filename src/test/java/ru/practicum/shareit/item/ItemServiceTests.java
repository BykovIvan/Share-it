package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingDto;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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

    @Test
    void saveItemTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);
        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        service.create(getUser.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));

    }


    @Test
    void updateItemTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);
        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto itemDtoGet = service.create(getUser.getId(), itemDto);

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
        service.addCommentToItem(getUser2.getId(), itemDtoGet.getId(), commentDto);

        ItemDtoWithComments itemDtoWithComments = service.findByUserIdAndItemId(getUser.getId(), itemDtoGet.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i join User u on i.owner.id = u.id where u.id = :ownerId and i.id = :id ", Item.class);
        Item item = query.setParameter("ownerId", getUser.getId()).setParameter("id", itemDtoGet.getId()).getSingleResult();


        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDtoWithComments.getName()));
        assertThat(item.getDescription(), equalTo(itemDtoWithComments.getDescription()));

    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
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

}
