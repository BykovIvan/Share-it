package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapping;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                             @RequestBody BookingDto bookingDto) {
        log.info("Получен запрос к эндпоинту /bookings. Метод POST");
        Booking booking = bookingService.create(userId, bookingDto);
        return BookingMapping.toBookingDto(booking);
    }

    /**
     * подтверждение бронирования вещи только владельцем вещи
     */
    @PatchMapping("/{id}")
    public BookingDto updateStatusOfItemById(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                           @PathVariable("id") Long bookingId,
                           @RequestParam(value = "approved") Boolean approved){
        log.info("Получен запрос к эндпоинту /bookings. Метод PATCH");
        Booking booking = bookingService.approvedStatusOfItem(userId, bookingId, approved);
        return BookingMapping.toBookingDto(booking);
    }

    @GetMapping("/{id}")
    public BookingDto findByIdOfOwnerOrBooker(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                               @PathVariable(value = "id") Long bookingId){
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по ID");
        return BookingMapping.toBookingDto(bookingService.findById(bookingId, userId));
    }

    //TODO Возможно будет List на выход!!!!!!
    @GetMapping()
    public BookingDto findByState(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                               @RequestParam(value = "state", required = false) String state){
        if (state == null){
            state = "ALL";
        }
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по State");
        return BookingMapping.toBookingDto(bookingService.findByState(state, userId));
    }
    @GetMapping("/owner")
    public BookingDto findByIdOfOwner(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                              @RequestParam(value = "state", required = false) String state){
        log.info("Получен запрос к эндпоинту /bookings. Метод GET по ID");
        return null;
    }

}
