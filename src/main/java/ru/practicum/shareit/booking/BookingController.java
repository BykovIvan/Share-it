package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                             @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту /bookings. Метод POST");
        return bookingService.create(userId, bookingDto);
    }

    // подтверждение бронирования вещи только владельцем вещи
    @PatchMapping("/{id}")
    public BookingDto updateStatusOfItemById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                             @PathVariable("id") Long bookingId,
                                             @RequestParam(value = "approved") Boolean approved) {
        log.info("Получен запрос к эндпоинту /bookings. Метод PATCH");
        return bookingService.approvedStatusOfItem(userId, bookingId, approved);
    }

    @GetMapping("/{id}")
    public BookingDto findByIdOfOwnerOrBooker(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                              @PathVariable(value = "id") Long bookingId) {
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по ID");
        return bookingService.findById(bookingId, userId);
    }

    //Получение списка всех бронирований текущего пользователя.
    @GetMapping()
    public List<BookingDto> findBookingByUserIdAndState(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                        @RequestParam(value = "state", required = false) String state,
                                                        @RequestParam(value = "from", required = false) Integer from,
                                                        @RequestParam(value = "size", required = false) Integer size) {
        if (state == null) {
            state = "ALL";
        }
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по State");
        return bookingService.findBookingByUserIdAndState(state, userId, from, size);
    }

    //Получение списка бронирований для всех вещей текущего пользователя.
    @GetMapping("/owner")
    public List<BookingDto> findItemByOwnerIdAndState(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                                      @RequestParam(value = "state", required = false) String state,
                                                      @RequestParam(value = "from", required = false) Integer from,
                                                      @RequestParam(value = "size", required = false) Integer size) {
        if (state == null) {
            state = "ALL";
        }
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по вещам владельца");
        return bookingService.findItemByOwnerIdAndState(state, userId, from, size);
    }

}
