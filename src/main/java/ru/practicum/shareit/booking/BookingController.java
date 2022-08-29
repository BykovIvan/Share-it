package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking create(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                          @RequestBody Booking booking) {
        log.info("Получен запрос к эндпоинту /bookings. Метод POST");
        return bookingService.create(userId, booking);
    }
}
