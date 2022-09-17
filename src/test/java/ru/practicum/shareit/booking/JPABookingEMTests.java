package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class JPABookingEMTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void listOfBookingsIsEmpty() {
        List<Booking> allBookings = repository.findAll();
        assertThat(allBookings).isEmpty();
    }

    @Test
    public void saveBookingTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .build());
        Booking booking = repository.save(Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis()))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build());
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    public void findByBookerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis() + 10000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.findByBookerIdByUserId(user.getId(), new Timestamp(System.currentTimeMillis() + 2000), Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }

    @Test
    public void findByOwnerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis() + 10000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.searchBookingsByOwnerId(user2.getId(), Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }

    @Test
    public void findCurrentByOwnerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis() + 10000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.searchBookingByOwnerIdCurrent(user2.getId(), new Timestamp(System.currentTimeMillis() + 2000), Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }

    @Test
    public void findInPastByOwnerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis() + 1000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.searchBookingsByOwnerIdPast(user2.getId(), new Timestamp(System.currentTimeMillis() + 20000), Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }

    @Test
    public void findInFutureByOwnerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis() + 10000))
                .end(new Timestamp(System.currentTimeMillis() + 20000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.searchBookingsByOwnerIdFuture(user2.getId(), new Timestamp(System.currentTimeMillis() + 2000), Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }

    @Test
    public void findByStatusAndByOwnerIdTest() {
        User user = userRepository.save(User.builder()
                .name("Ivan")
                .email("ivan@yandex.ru")
                .build());
        User user2 = userRepository.save(User.builder()
                .name("Ivan2")
                .email("ivan2@yandex.ru")
                .build());
        Item item = itemRepository.save(Item.builder()
                .name("Car")
                .description("Car for you")
                .available(true)
                .owner(user2)
                .build());
        Booking booking = Booking.builder()
                .start(new Timestamp(System.currentTimeMillis()))
                .end(new Timestamp(System.currentTimeMillis() + 10000))
                .booker(user)
                .item(item)
                .status(StatusOfItem.WAITING)
                .build();
        entityManager.persist(booking);

        List<Booking> list = repository.searchBookingsByOwnerIdWaitingAndRejected(user2.getId(), StatusOfItem.WAITING, Sort.by(Sort.Direction.DESC, "id"));
        assertThat(list).hasSize(1).contains(booking);
    }


}
