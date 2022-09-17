package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto findById(Long id, Long userId);

    List<BookingDto> findBookingByUserIdAndState(String state, Long userId, Integer from, Integer size);

    BookingDto approvedStatusOfItem(Long userId, Long bookingId, Boolean approved);

    List<BookingDto> findItemByOwnerIdAndState(String state, Long userId, Integer from, Integer size);
}
