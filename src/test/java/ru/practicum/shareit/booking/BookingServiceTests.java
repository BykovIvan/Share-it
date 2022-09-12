package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTests {

    private final EntityManager em;
    private final BookingService service;
    private final ItemService itemService;
    private final UserService userService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void saveBookingTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(bookingCreate.getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(bookingCreate.getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(bookingCreate.getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(bookingCreate.getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(bookingCreate.getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(bookingCreate.getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(bookingCreate.getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(bookingCreate.getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(bookingCreate.getStatus()));
    }

    @Test
    void findBookingByIdTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        BookingDto getBookingById = service.findById(bookingCreate.getId(), getUser.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingById.getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingById.getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingById.getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingById.getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingById.getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingById.getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingById.getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingById.getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingById.getStatus()));
    }

    @Test
    void findBookingByUserIdAndStateTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        List<BookingDto> getBookingByState = service.findBookingByUserIdAndState("ALL", getUser2.getId(), 0, 1);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByState.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByState.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByState.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByState.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByState.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByState.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByState.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByState.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByState.get(0).getStatus()));
    }

    @Test
    void approveStatusOfBookingTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        BookingDto getBookingWithApproved = service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingWithApproved.getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingWithApproved.getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingWithApproved.getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingWithApproved.getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingWithApproved.getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingWithApproved.getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingWithApproved.getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingWithApproved.getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingWithApproved.getStatus()));
    }

    @Test
    void findBookingByOwnerIdAndStateTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        List<BookingDto> getBookingByState = service.findItemByOwnerIdAndState("ALL", getUser.getId(), 0, 1);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(booking.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByState.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByState.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByState.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByState.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByState.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByState.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByState.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByState.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByState.get(0).getStatus()));
    }

    private BookingDto makeBookingDto(ItemDto itemDto, UserDto booker){
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

}
