package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking create(@RequestBody Booking booking) {
//        log.info("Получен запрос к эндпоинту /users. Метод POST");
//        User user = UserMapping.toUser(userDto);
//        return UserMapping.toUserDto(bookingService.save(user));
//        return bookingService.save(booking);
        return null;
    }
}
