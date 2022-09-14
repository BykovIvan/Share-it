package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.create(1L, new BookingDto());
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        UserDto userDto3 = makeUserDto("Пётр3", "some3@email.com");
        UserDto getUser3 = userService.create(userDto3);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        ItemDto itemDto2 = makeItemDto("Hammer2", "Hammer2 for test", false);
        ItemDto getItem2 = itemService.create(getUser.getId(), itemDto2);

        BookingDto bookingDtoErrorItemId = makeBookingDtoErrorItemId(getUser3);

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.create(getUser3.getId(), bookingDtoErrorItemId);
        });
        String expectedMessage2 = "Такой вещи не существует!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        Exception exception3 = assertThrows(BadRequestException.class, () -> {
            BookingDto bookingDto = makeBookingDtoStartInPast(getItem, getUser);
            service.create(getUser2.getId(), bookingDto);
        });
        String expectedMessage3 = "Время начала не может быть в прошлом!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        Exception exception4 = assertThrows(BadRequestException.class, () -> {
            BookingDto bookingDto = makeBookingDtoEndInPast(getItem, getUser);
            service.create(getUser2.getId(), bookingDto);
        });
        String expectedMessage4 = "Время окончания не может быть в прошлом!";
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage4));

        Exception exception5 = assertThrows(BadRequestException.class, () -> {
            BookingDto bookingDto = makeBookingDtoStartAfterEnd(getItem, getUser);
            service.create(getUser2.getId(), bookingDto);
        });
        String expectedMessage5 = "Время окончания не может быть раньше начала бронирования!";
        String actualMessage5 = exception5.getMessage();
        assertTrue(actualMessage5.contains(expectedMessage5));

        Exception exception6 = assertThrows(NotFoundException.class, () -> {
            BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
            service.create(getUser.getId(), bookingDto);
        });
        String expectedMessage6 = "Владелец не может забронировать свою вещь!";
        String actualMessage6 = exception6.getMessage();
        assertTrue(actualMessage6.contains(expectedMessage6));

        Exception exception7 = assertThrows(BadRequestException.class, () -> {
            BookingDto bookingDto = makeBookingDto(getItem2, getUser, StatusOfItem.WAITING);
            service.create(getUser2.getId(), bookingDto);
        });
        String expectedMessage7 = "Вещь не доступна!";
        String actualMessage7 = exception7.getMessage();
        assertTrue(actualMessage7.contains(expectedMessage7));

        BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
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

        Exception exception8 = assertThrows(BadRequestException.class, () -> {
            BookingDto bookingDto8 = makeBookingDto(getItem, getUser3, StatusOfItem.WAITING);
            service.create(getUser3.getId(), bookingDto8);
        });
        String expectedMessage8 = "Вещь в данный переод времени забронирована!";
        String actualMessage8 = exception8.getMessage();
        assertTrue(actualMessage8.contains(expectedMessage8));

    }

    @Test
    void approveStatusOfBookingTest() {
        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.approvedStatusOfItem(getUser.getId(), 22L, true);
        });
        String expectedMessage = "Такое бронирование не найдено!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        Exception exception2 = assertThrows(NotFoundException.class, () -> {
            service.approvedStatusOfItem(getUser2.getId(), bookingCreate.getId(), true);
        });
        String expectedMessage2 = "Пользователь не является владельцем вещи!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

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

        Exception exception3 = assertThrows(BadRequestException.class, () -> {
            service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), true);
        });
        String expectedMessage3 = "Статус уже подтвержден!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        BookingDto getBookingWithRejected = service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), false);

        TypedQuery<Booking> query2 = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking2 = query2.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet2 = BookingMapping.toBookingDto(booking2);

        assertThat(bookingDtoGet2.getStatus(), equalTo(getBookingWithRejected.getStatus()));

        Exception exception4 = assertThrows(BadRequestException.class, () -> {
            service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), false);
        });
        String expectedMessage4 = "Статус уже не подтвержден!";
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage4));
    }

    @Test
    void findBookingByIdTest() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.findById(1L, 1L);
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        Exception exception2 = assertThrows(NoUserInHeaderException.class, () -> {
            service.findById(null, getUser.getId());
        });
        String expectedMessage2 = "Отсутсвует id бронирования в запросе!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        Exception exception3 = assertThrows(NotFoundException.class, () -> {
            service.findById(22L, getUser.getId());
        });
        String expectedMessage3 = "Нет такого бронирования!";
        String actualMessage3 = exception3.getMessage();
        assertTrue(actualMessage3.contains(expectedMessage3));

        UserDto userDto3 = makeUserDto("Пётр3", "some3@email.com");
        UserDto getUser3 = userService.create(userDto3);

        Exception exception4 = assertThrows(NotFoundException.class, () -> {
            service.findById(bookingCreate.getId(), getUser3.getId());
        });
        String expectedMessage4 = "Не является владельцем или арентадателем вещи!";
        String actualMessage4 = exception4.getMessage();
        assertTrue(actualMessage4.contains(expectedMessage4));

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
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.findBookingByUserIdAndState("ALL", 1L, 0, 1);
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        Exception exception2 = assertThrows(BadRequestException.class, () -> {
            service.findBookingByUserIdAndState("ALL", getUser2.getId(), -1, 0);
        });
        String expectedMessage2 = "Введены неверные параметры!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

        List<BookingDto> getBookingByState = service.findBookingByUserIdAndState("ALL", getUser2.getId(), 0, 1);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGet = BookingMapping.toBookingDto(booking);

        assertThat(bookingDtoGet.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByState.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByState.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByState.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByState.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByState.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByState.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByState.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByState.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByState.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNull = service.findBookingByUserIdAndState("ALL", getUser2.getId(), null, null);

        assertThat(bookingDtoGet.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByStateWithNull.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByStateWithNull.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByStateWithNull.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByStateWithNull.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByStateWithNull.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByStateWithNull.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByStateWithNull.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByStateWithNull.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByStateWithNull.get(0).getStatus()));

        List<BookingDto> getBookingByStateFuture = service.findBookingByUserIdAndState("FUTURE", getUser2.getId(), 0, 1);

        TypedQuery<Booking> queryFuture = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingFuture = queryFuture.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetFuture = BookingMapping.toBookingDto(bookingFuture);

        assertThat(bookingDtoGetFuture.getId(), notNullValue());
        assertThat(bookingDtoGetFuture.getStart().format(formatter), equalTo(getBookingByStateFuture.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetFuture.getEnd().format(formatter), equalTo(getBookingByStateFuture.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetFuture.getItem().getId(), equalTo(getBookingByStateFuture.get(0).getItem().getId()));
        assertThat(bookingDtoGetFuture.getItem().getName(), equalTo(getBookingByStateFuture.get(0).getItem().getName()));
        assertThat(bookingDtoGetFuture.getItemId(), equalTo(getBookingByStateFuture.get(0).getItemId()));
        assertThat(bookingDtoGetFuture.getBooker().getId(), equalTo(getBookingByStateFuture.get(0).getBooker().getId()));
        assertThat(bookingDtoGetFuture.getBooker().getName(), equalTo(getBookingByStateFuture.get(0).getBooker().getName()));
        assertThat(bookingDtoGetFuture.getBooker().getEmail(), equalTo(getBookingByStateFuture.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetFuture.getStatus(), equalTo(getBookingByStateFuture.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullFuture = service.findBookingByUserIdAndState("FUTURE", getUser2.getId(), null, null);

        assertThat(bookingDtoGetFuture.getId(), notNullValue());
        assertThat(bookingDtoGetFuture.getStart().format(formatter), equalTo(getBookingByStateWithNullFuture.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetFuture.getEnd().format(formatter), equalTo(getBookingByStateWithNullFuture.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetFuture.getItem().getId(), equalTo(getBookingByStateWithNullFuture.get(0).getItem().getId()));
        assertThat(bookingDtoGetFuture.getItem().getName(), equalTo(getBookingByStateWithNullFuture.get(0).getItem().getName()));
        assertThat(bookingDtoGetFuture.getItemId(), equalTo(getBookingByStateWithNullFuture.get(0).getItemId()));
        assertThat(bookingDtoGetFuture.getBooker().getId(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getId()));
        assertThat(bookingDtoGetFuture.getBooker().getName(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getName()));
        assertThat(bookingDtoGetFuture.getBooker().getEmail(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetFuture.getStatus(), equalTo(getBookingByStateWithNullFuture.get(0).getStatus()));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<BookingDto> getBookingByStateCurrent = service.findBookingByUserIdAndState("CURRENT", getUser2.getId(), 0, 1);

        TypedQuery<Booking> queryCurrent = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingCurrent = queryCurrent.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetCurrent = BookingMapping.toBookingDto(bookingCurrent);

        assertThat(bookingDtoGetCurrent.getId(), notNullValue());
        assertThat(bookingDtoGetCurrent.getStart().format(formatter), equalTo(getBookingByStateCurrent.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetCurrent.getEnd().format(formatter), equalTo(getBookingByStateCurrent.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetCurrent.getItem().getId(), equalTo(getBookingByStateCurrent.get(0).getItem().getId()));
        assertThat(bookingDtoGetCurrent.getItem().getName(), equalTo(getBookingByStateCurrent.get(0).getItem().getName()));
        assertThat(bookingDtoGetCurrent.getItemId(), equalTo(getBookingByStateCurrent.get(0).getItemId()));
        assertThat(bookingDtoGetCurrent.getBooker().getId(), equalTo(getBookingByStateCurrent.get(0).getBooker().getId()));
        assertThat(bookingDtoGetCurrent.getBooker().getName(), equalTo(getBookingByStateCurrent.get(0).getBooker().getName()));
        assertThat(bookingDtoGetCurrent.getBooker().getEmail(), equalTo(getBookingByStateCurrent.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetCurrent.getStatus(), equalTo(getBookingByStateCurrent.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullCurrent = service.findBookingByUserIdAndState("CURRENT", getUser2.getId(), null, null);

        assertThat(bookingDtoGetCurrent.getId(), notNullValue());
        assertThat(bookingDtoGetCurrent.getStart().format(formatter), equalTo(getBookingByStateWithNullCurrent.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetCurrent.getEnd().format(formatter), equalTo(getBookingByStateWithNullCurrent.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetCurrent.getItem().getId(), equalTo(getBookingByStateWithNullCurrent.get(0).getItem().getId()));
        assertThat(bookingDtoGetCurrent.getItem().getName(), equalTo(getBookingByStateWithNullCurrent.get(0).getItem().getName()));
        assertThat(bookingDtoGetCurrent.getItemId(), equalTo(getBookingByStateWithNullCurrent.get(0).getItemId()));
        assertThat(bookingDtoGetCurrent.getBooker().getId(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getId()));
        assertThat(bookingDtoGetCurrent.getBooker().getName(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getName()));
        assertThat(bookingDtoGetCurrent.getBooker().getEmail(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetCurrent.getStatus(), equalTo(getBookingByStateWithNullCurrent.get(0).getStatus()));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<BookingDto> getBookingByStatePast = service.findBookingByUserIdAndState("PAST", getUser2.getId(), 0, 1);

        TypedQuery<Booking> queryPast = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingPast = queryPast.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetPast = BookingMapping.toBookingDto(bookingPast);

        assertThat(bookingDtoGetPast.getId(), notNullValue());
        assertThat(bookingDtoGetPast.getStart().format(formatter), equalTo(getBookingByStatePast.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetPast.getEnd().format(formatter), equalTo(getBookingByStatePast.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetPast.getItem().getId(), equalTo(getBookingByStatePast.get(0).getItem().getId()));
        assertThat(bookingDtoGetPast.getItem().getName(), equalTo(getBookingByStatePast.get(0).getItem().getName()));
        assertThat(bookingDtoGetPast.getItemId(), equalTo(getBookingByStatePast.get(0).getItemId()));
        assertThat(bookingDtoGetPast.getBooker().getId(), equalTo(getBookingByStatePast.get(0).getBooker().getId()));
        assertThat(bookingDtoGetPast.getBooker().getName(), equalTo(getBookingByStatePast.get(0).getBooker().getName()));
        assertThat(bookingDtoGetPast.getBooker().getEmail(), equalTo(getBookingByStatePast.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetPast.getStatus(), equalTo(getBookingByStatePast.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullPast = service.findBookingByUserIdAndState("PAST", getUser2.getId(), null, null);

        assertThat(bookingDtoGetPast.getId(), notNullValue());
        assertThat(bookingDtoGetPast.getStart().format(formatter), equalTo(getBookingByStateWithNullPast.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetPast.getEnd().format(formatter), equalTo(getBookingByStateWithNullPast.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetPast.getItem().getId(), equalTo(getBookingByStateWithNullPast.get(0).getItem().getId()));
        assertThat(bookingDtoGetPast.getItem().getName(), equalTo(getBookingByStateWithNullPast.get(0).getItem().getName()));
        assertThat(bookingDtoGetPast.getItemId(), equalTo(getBookingByStateWithNullPast.get(0).getItemId()));
        assertThat(bookingDtoGetPast.getBooker().getId(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getId()));
        assertThat(bookingDtoGetPast.getBooker().getName(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getName()));
        assertThat(bookingDtoGetPast.getBooker().getEmail(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetPast.getStatus(), equalTo(getBookingByStateWithNullPast.get(0).getStatus()));

        List<BookingDto> getBookingByStateWaiting = service.findBookingByUserIdAndState("WAITING", getUser2.getId(), 0, 1);

        TypedQuery<Booking> queryWaiting = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingWaiting = queryWaiting.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetWaiting = BookingMapping.toBookingDto(bookingWaiting);

        assertThat(bookingDtoGetWaiting.getId(), notNullValue());
        assertThat(bookingDtoGetWaiting.getStart().format(formatter), equalTo(getBookingByStateWaiting.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetWaiting.getEnd().format(formatter), equalTo(getBookingByStateWaiting.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetWaiting.getItem().getId(), equalTo(getBookingByStateWaiting.get(0).getItem().getId()));
        assertThat(bookingDtoGetWaiting.getItem().getName(), equalTo(getBookingByStateWaiting.get(0).getItem().getName()));
        assertThat(bookingDtoGetWaiting.getItemId(), equalTo(getBookingByStateWaiting.get(0).getItemId()));
        assertThat(bookingDtoGetWaiting.getBooker().getId(), equalTo(getBookingByStateWaiting.get(0).getBooker().getId()));
        assertThat(bookingDtoGetWaiting.getBooker().getName(), equalTo(getBookingByStateWaiting.get(0).getBooker().getName()));
        assertThat(bookingDtoGetWaiting.getBooker().getEmail(), equalTo(getBookingByStateWaiting.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetWaiting.getStatus(), equalTo(getBookingByStateWaiting.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullWaiting = service.findBookingByUserIdAndState("WAITING", getUser2.getId(), null, null);

        assertThat(bookingDtoGetWaiting.getId(), notNullValue());
        assertThat(bookingDtoGetWaiting.getStart().format(formatter), equalTo(getBookingByStateWithNullWaiting.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetWaiting.getEnd().format(formatter), equalTo(getBookingByStateWithNullWaiting.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetWaiting.getItem().getId(), equalTo(getBookingByStateWithNullWaiting.get(0).getItem().getId()));
        assertThat(bookingDtoGetWaiting.getItem().getName(), equalTo(getBookingByStateWithNullWaiting.get(0).getItem().getName()));
        assertThat(bookingDtoGetWaiting.getItemId(), equalTo(getBookingByStateWithNullWaiting.get(0).getItemId()));
        assertThat(bookingDtoGetWaiting.getBooker().getId(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getId()));
        assertThat(bookingDtoGetWaiting.getBooker().getName(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getName()));
        assertThat(bookingDtoGetWaiting.getBooker().getEmail(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetWaiting.getStatus(), equalTo(getBookingByStateWithNullWaiting.get(0).getStatus()));

        service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), false);

        List<BookingDto> getBookingByStateRejected = service.findBookingByUserIdAndState("REJECTED", getUser2.getId(), 0, 1);

        TypedQuery<Booking> queryRejected = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingRejected = queryRejected.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetRejected = BookingMapping.toBookingDto(bookingRejected);

        assertThat(bookingDtoGetRejected.getId(), notNullValue());
        assertThat(bookingDtoGetRejected.getStart().format(formatter), equalTo(getBookingByStateRejected.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetRejected.getEnd().format(formatter), equalTo(getBookingByStateRejected.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetRejected.getItem().getId(), equalTo(getBookingByStateRejected.get(0).getItem().getId()));
        assertThat(bookingDtoGetRejected.getItem().getName(), equalTo(getBookingByStateRejected.get(0).getItem().getName()));
        assertThat(bookingDtoGetRejected.getItemId(), equalTo(getBookingByStateRejected.get(0).getItemId()));
        assertThat(bookingDtoGetRejected.getBooker().getId(), equalTo(getBookingByStateRejected.get(0).getBooker().getId()));
        assertThat(bookingDtoGetRejected.getBooker().getName(), equalTo(getBookingByStateRejected.get(0).getBooker().getName()));
        assertThat(bookingDtoGetRejected.getBooker().getEmail(), equalTo(getBookingByStateRejected.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetRejected.getStatus(), equalTo(getBookingByStateRejected.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullRejected = service.findBookingByUserIdAndState("REJECTED", getUser2.getId(), null, null);

        assertThat(bookingDtoGetRejected.getId(), notNullValue());
        assertThat(bookingDtoGetRejected.getStart().format(formatter), equalTo(getBookingByStateWithNullRejected.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetRejected.getEnd().format(formatter), equalTo(getBookingByStateWithNullRejected.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetRejected.getItem().getId(), equalTo(getBookingByStateWithNullRejected.get(0).getItem().getId()));
        assertThat(bookingDtoGetRejected.getItem().getName(), equalTo(getBookingByStateWithNullRejected.get(0).getItem().getName()));
        assertThat(bookingDtoGetRejected.getItemId(), equalTo(getBookingByStateWithNullRejected.get(0).getItemId()));
        assertThat(bookingDtoGetRejected.getBooker().getId(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getId()));
        assertThat(bookingDtoGetRejected.getBooker().getName(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getName()));
        assertThat(bookingDtoGetRejected.getBooker().getEmail(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetRejected.getStatus(), equalTo(getBookingByStateWithNullRejected.get(0).getStatus()));

    }

    @Test
    void findBookingByOwnerIdAndStateTest() {
        Exception exception = assertThrows(NotFoundException.class, () -> {
            service.findItemByOwnerIdAndState("ALL", 1L, 0, 1);
        });
        String expectedMessage = "Такого пользователя не существует!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

        UserDto userDto = makeUserDto("Пётр", "some@email.com");
        UserDto getUser = userService.create(userDto);

        UserDto userDto2 = makeUserDto("Пётр2", "some2@email.com");
        UserDto getUser2 = userService.create(userDto2);

        ItemDto itemDto = makeItemDto("Hammer", "Hammer for test", true);
        ItemDto getItem = itemService.create(getUser.getId(), itemDto);

        BookingDto bookingDto = makeBookingDto(getItem, getUser, StatusOfItem.WAITING);
        BookingDto bookingCreate = service.create(getUser2.getId(), bookingDto);

        Exception exception2 = assertThrows(BadRequestException.class, () -> {
            service.findItemByOwnerIdAndState("ALL", getUser.getId(), -1, 0);
        });
        String expectedMessage2 = "Введены неверные параметры!";
        String actualMessage2 = exception2.getMessage();
        assertTrue(actualMessage2.contains(expectedMessage2));

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

        assertThat(bookingDtoGet.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByState.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByState.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByState.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByState.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByState.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByState.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByState.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByState.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByState.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNull = service.findItemByOwnerIdAndState("ALL", getUser.getId(), null, null);

        assertThat(bookingDtoGet.getId(), notNullValue());
        assertThat(bookingDtoGet.getStart().format(formatter), equalTo(getBookingByStateWithNull.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGet.getEnd().format(formatter), equalTo(getBookingByStateWithNull.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGet.getItem().getId(), equalTo(getBookingByStateWithNull.get(0).getItem().getId()));
        assertThat(bookingDtoGet.getItem().getName(), equalTo(getBookingByStateWithNull.get(0).getItem().getName()));
        assertThat(bookingDtoGet.getItemId(), equalTo(getBookingByStateWithNull.get(0).getItemId()));
        assertThat(bookingDtoGet.getBooker().getId(), equalTo(getBookingByStateWithNull.get(0).getBooker().getId()));
        assertThat(bookingDtoGet.getBooker().getName(), equalTo(getBookingByStateWithNull.get(0).getBooker().getName()));
        assertThat(bookingDtoGet.getBooker().getEmail(), equalTo(getBookingByStateWithNull.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGet.getStatus(), equalTo(getBookingByStateWithNull.get(0).getStatus()));

        List<BookingDto> getBookingByStateFuture = service.findItemByOwnerIdAndState("FUTURE", getUser.getId(), 0, 1);

        TypedQuery<Booking> queryFuture = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingFuture = queryFuture.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetFuture = BookingMapping.toBookingDto(bookingFuture);

        assertThat(bookingDtoGetFuture.getId(), notNullValue());
        assertThat(bookingDtoGetFuture.getStart().format(formatter), equalTo(getBookingByStateFuture.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetFuture.getEnd().format(formatter), equalTo(getBookingByStateFuture.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetFuture.getItem().getId(), equalTo(getBookingByStateFuture.get(0).getItem().getId()));
        assertThat(bookingDtoGetFuture.getItem().getName(), equalTo(getBookingByStateFuture.get(0).getItem().getName()));
        assertThat(bookingDtoGetFuture.getItemId(), equalTo(getBookingByStateFuture.get(0).getItemId()));
        assertThat(bookingDtoGetFuture.getBooker().getId(), equalTo(getBookingByStateFuture.get(0).getBooker().getId()));
        assertThat(bookingDtoGetFuture.getBooker().getName(), equalTo(getBookingByStateFuture.get(0).getBooker().getName()));
        assertThat(bookingDtoGetFuture.getBooker().getEmail(), equalTo(getBookingByStateFuture.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetFuture.getStatus(), equalTo(getBookingByStateFuture.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullFuture = service.findItemByOwnerIdAndState("FUTURE", getUser.getId(), null, null);

        assertThat(bookingDtoGetFuture.getId(), notNullValue());
        assertThat(bookingDtoGetFuture.getStart().format(formatter), equalTo(getBookingByStateWithNullFuture.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetFuture.getEnd().format(formatter), equalTo(getBookingByStateWithNullFuture.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetFuture.getItem().getId(), equalTo(getBookingByStateWithNullFuture.get(0).getItem().getId()));
        assertThat(bookingDtoGetFuture.getItem().getName(), equalTo(getBookingByStateWithNullFuture.get(0).getItem().getName()));
        assertThat(bookingDtoGetFuture.getItemId(), equalTo(getBookingByStateWithNullFuture.get(0).getItemId()));
        assertThat(bookingDtoGetFuture.getBooker().getId(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getId()));
        assertThat(bookingDtoGetFuture.getBooker().getName(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getName()));
        assertThat(bookingDtoGetFuture.getBooker().getEmail(), equalTo(getBookingByStateWithNullFuture.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetFuture.getStatus(), equalTo(getBookingByStateWithNullFuture.get(0).getStatus()));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<BookingDto> getBookingByStateCurrent = service.findItemByOwnerIdAndState("CURRENT", getUser.getId(), 0, 1);

        TypedQuery<Booking> queryCurrent = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingCurrent = queryCurrent.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetCurrent = BookingMapping.toBookingDto(bookingCurrent);

        assertThat(bookingDtoGetCurrent.getId(), notNullValue());
        assertThat(bookingDtoGetCurrent.getStart().format(formatter), equalTo(getBookingByStateCurrent.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetCurrent.getEnd().format(formatter), equalTo(getBookingByStateCurrent.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetCurrent.getItem().getId(), equalTo(getBookingByStateCurrent.get(0).getItem().getId()));
        assertThat(bookingDtoGetCurrent.getItem().getName(), equalTo(getBookingByStateCurrent.get(0).getItem().getName()));
        assertThat(bookingDtoGetCurrent.getItemId(), equalTo(getBookingByStateCurrent.get(0).getItemId()));
        assertThat(bookingDtoGetCurrent.getBooker().getId(), equalTo(getBookingByStateCurrent.get(0).getBooker().getId()));
        assertThat(bookingDtoGetCurrent.getBooker().getName(), equalTo(getBookingByStateCurrent.get(0).getBooker().getName()));
        assertThat(bookingDtoGetCurrent.getBooker().getEmail(), equalTo(getBookingByStateCurrent.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetCurrent.getStatus(), equalTo(getBookingByStateCurrent.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullCurrent = service.findItemByOwnerIdAndState("CURRENT", getUser.getId(), null, null);

        assertThat(bookingDtoGetCurrent.getId(), notNullValue());
        assertThat(bookingDtoGetCurrent.getStart().format(formatter), equalTo(getBookingByStateWithNullCurrent.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetCurrent.getEnd().format(formatter), equalTo(getBookingByStateWithNullCurrent.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetCurrent.getItem().getId(), equalTo(getBookingByStateWithNullCurrent.get(0).getItem().getId()));
        assertThat(bookingDtoGetCurrent.getItem().getName(), equalTo(getBookingByStateWithNullCurrent.get(0).getItem().getName()));
        assertThat(bookingDtoGetCurrent.getItemId(), equalTo(getBookingByStateWithNullCurrent.get(0).getItemId()));
        assertThat(bookingDtoGetCurrent.getBooker().getId(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getId()));
        assertThat(bookingDtoGetCurrent.getBooker().getName(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getName()));
        assertThat(bookingDtoGetCurrent.getBooker().getEmail(), equalTo(getBookingByStateWithNullCurrent.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetCurrent.getStatus(), equalTo(getBookingByStateWithNullCurrent.get(0).getStatus()));

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        List<BookingDto> getBookingByStatePast = service.findItemByOwnerIdAndState("PAST", getUser.getId(), 0, 1);

        TypedQuery<Booking> queryPast = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingPast = queryPast.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetPast = BookingMapping.toBookingDto(bookingPast);

        assertThat(bookingDtoGetPast.getId(), notNullValue());
        assertThat(bookingDtoGetPast.getStart().format(formatter), equalTo(getBookingByStatePast.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetPast.getEnd().format(formatter), equalTo(getBookingByStatePast.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetPast.getItem().getId(), equalTo(getBookingByStatePast.get(0).getItem().getId()));
        assertThat(bookingDtoGetPast.getItem().getName(), equalTo(getBookingByStatePast.get(0).getItem().getName()));
        assertThat(bookingDtoGetPast.getItemId(), equalTo(getBookingByStatePast.get(0).getItemId()));
        assertThat(bookingDtoGetPast.getBooker().getId(), equalTo(getBookingByStatePast.get(0).getBooker().getId()));
        assertThat(bookingDtoGetPast.getBooker().getName(), equalTo(getBookingByStatePast.get(0).getBooker().getName()));
        assertThat(bookingDtoGetPast.getBooker().getEmail(), equalTo(getBookingByStatePast.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetPast.getStatus(), equalTo(getBookingByStatePast.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullPast = service.findItemByOwnerIdAndState("PAST", getUser.getId(), null, null);

        assertThat(bookingDtoGetPast.getId(), notNullValue());
        assertThat(bookingDtoGetPast.getStart().format(formatter), equalTo(getBookingByStateWithNullPast.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetPast.getEnd().format(formatter), equalTo(getBookingByStateWithNullPast.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetPast.getItem().getId(), equalTo(getBookingByStateWithNullPast.get(0).getItem().getId()));
        assertThat(bookingDtoGetPast.getItem().getName(), equalTo(getBookingByStateWithNullPast.get(0).getItem().getName()));
        assertThat(bookingDtoGetPast.getItemId(), equalTo(getBookingByStateWithNullPast.get(0).getItemId()));
        assertThat(bookingDtoGetPast.getBooker().getId(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getId()));
        assertThat(bookingDtoGetPast.getBooker().getName(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getName()));
        assertThat(bookingDtoGetPast.getBooker().getEmail(), equalTo(getBookingByStateWithNullPast.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetPast.getStatus(), equalTo(getBookingByStateWithNullPast.get(0).getStatus()));

        List<BookingDto> getBookingByStateWaiting = service.findItemByOwnerIdAndState("WAITING", getUser.getId(), 0, 1);

        TypedQuery<Booking> queryWaiting = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingWaiting = queryWaiting.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetWaiting = BookingMapping.toBookingDto(bookingWaiting);

        assertThat(bookingDtoGetWaiting.getId(), notNullValue());
        assertThat(bookingDtoGetWaiting.getStart().format(formatter), equalTo(getBookingByStateWaiting.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetWaiting.getEnd().format(formatter), equalTo(getBookingByStateWaiting.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetWaiting.getItem().getId(), equalTo(getBookingByStateWaiting.get(0).getItem().getId()));
        assertThat(bookingDtoGetWaiting.getItem().getName(), equalTo(getBookingByStateWaiting.get(0).getItem().getName()));
        assertThat(bookingDtoGetWaiting.getItemId(), equalTo(getBookingByStateWaiting.get(0).getItemId()));
        assertThat(bookingDtoGetWaiting.getBooker().getId(), equalTo(getBookingByStateWaiting.get(0).getBooker().getId()));
        assertThat(bookingDtoGetWaiting.getBooker().getName(), equalTo(getBookingByStateWaiting.get(0).getBooker().getName()));
        assertThat(bookingDtoGetWaiting.getBooker().getEmail(), equalTo(getBookingByStateWaiting.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetWaiting.getStatus(), equalTo(getBookingByStateWaiting.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullWaiting = service.findItemByOwnerIdAndState("WAITING", getUser.getId(), null, null);

        assertThat(bookingDtoGetWaiting.getId(), notNullValue());
        assertThat(bookingDtoGetWaiting.getStart().format(formatter), equalTo(getBookingByStateWithNullWaiting.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetWaiting.getEnd().format(formatter), equalTo(getBookingByStateWithNullWaiting.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetWaiting.getItem().getId(), equalTo(getBookingByStateWithNullWaiting.get(0).getItem().getId()));
        assertThat(bookingDtoGetWaiting.getItem().getName(), equalTo(getBookingByStateWithNullWaiting.get(0).getItem().getName()));
        assertThat(bookingDtoGetWaiting.getItemId(), equalTo(getBookingByStateWithNullWaiting.get(0).getItemId()));
        assertThat(bookingDtoGetWaiting.getBooker().getId(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getId()));
        assertThat(bookingDtoGetWaiting.getBooker().getName(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getName()));
        assertThat(bookingDtoGetWaiting.getBooker().getEmail(), equalTo(getBookingByStateWithNullWaiting.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetWaiting.getStatus(), equalTo(getBookingByStateWithNullWaiting.get(0).getStatus()));

        service.approvedStatusOfItem(getUser.getId(), bookingCreate.getId(), false);

        List<BookingDto> getBookingByStateRejected = service.findItemByOwnerIdAndState("REJECTED", getUser.getId(), 0, 1);

        TypedQuery<Booking> queryRejected = em.createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking bookingRejected = queryRejected.setParameter("id", bookingCreate.getId()).getSingleResult();
        BookingDto bookingDtoGetRejected = BookingMapping.toBookingDto(bookingRejected);

        assertThat(bookingDtoGetRejected.getId(), notNullValue());
        assertThat(bookingDtoGetRejected.getStart().format(formatter), equalTo(getBookingByStateRejected.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetRejected.getEnd().format(formatter), equalTo(getBookingByStateRejected.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetRejected.getItem().getId(), equalTo(getBookingByStateRejected.get(0).getItem().getId()));
        assertThat(bookingDtoGetRejected.getItem().getName(), equalTo(getBookingByStateRejected.get(0).getItem().getName()));
        assertThat(bookingDtoGetRejected.getItemId(), equalTo(getBookingByStateRejected.get(0).getItemId()));
        assertThat(bookingDtoGetRejected.getBooker().getId(), equalTo(getBookingByStateRejected.get(0).getBooker().getId()));
        assertThat(bookingDtoGetRejected.getBooker().getName(), equalTo(getBookingByStateRejected.get(0).getBooker().getName()));
        assertThat(bookingDtoGetRejected.getBooker().getEmail(), equalTo(getBookingByStateRejected.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetRejected.getStatus(), equalTo(getBookingByStateRejected.get(0).getStatus()));

        List<BookingDto> getBookingByStateWithNullRejected = service.findItemByOwnerIdAndState("REJECTED", getUser.getId(), null, null);

        assertThat(bookingDtoGetRejected.getId(), notNullValue());
        assertThat(bookingDtoGetRejected.getStart().format(formatter), equalTo(getBookingByStateWithNullRejected.get(0).getStart().format(formatter)));
        assertThat(bookingDtoGetRejected.getEnd().format(formatter), equalTo(getBookingByStateWithNullRejected.get(0).getEnd().format(formatter)));
        assertThat(bookingDtoGetRejected.getItem().getId(), equalTo(getBookingByStateWithNullRejected.get(0).getItem().getId()));
        assertThat(bookingDtoGetRejected.getItem().getName(), equalTo(getBookingByStateWithNullRejected.get(0).getItem().getName()));
        assertThat(bookingDtoGetRejected.getItemId(), equalTo(getBookingByStateWithNullRejected.get(0).getItemId()));
        assertThat(bookingDtoGetRejected.getBooker().getId(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getId()));
        assertThat(bookingDtoGetRejected.getBooker().getName(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getName()));
        assertThat(bookingDtoGetRejected.getBooker().getEmail(), equalTo(getBookingByStateWithNullRejected.get(0).getBooker().getEmail()));
        assertThat(bookingDtoGetRejected.getStatus(), equalTo(getBookingByStateWithNullRejected.get(0).getStatus()));




    }

    private BookingDto makeBookingDto(ItemDto itemDto, UserDto booker, StatusOfItem status) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(4));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(8));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(status);
        return bookingDto;
    }

    private BookingDto makeBookingDtoStartInPast(ItemDto itemDto, UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().minusMinutes(10));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(StatusOfItem.WAITING);
        return bookingDto;
    }

    private BookingDto makeBookingDtoEndInPast(ItemDto itemDto, UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().minusMinutes(4));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(StatusOfItem.WAITING);
        return bookingDto;
    }

    private BookingDto makeBookingDtoStartAfterEnd(ItemDto itemDto, UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(6));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingDto.setItem(itemDto);
        bookingDto.setItemId(itemDto.getId());
        bookingDto.setOwner(1L);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(StatusOfItem.WAITING);
        return bookingDto;
    }

    private BookingDto makeBookingDtoErrorItemId(UserDto booker) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingDto.setEnd(LocalDateTime.now().plusSeconds(2));
        bookingDto.setItemId(22L);
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
