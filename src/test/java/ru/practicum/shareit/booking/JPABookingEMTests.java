package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
    private TestEntityManager em;

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
    public void saveItem() {
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

}
