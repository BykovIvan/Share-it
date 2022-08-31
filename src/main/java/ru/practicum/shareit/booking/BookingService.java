package ru.practicum.shareit.booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking create(Long userId, BookingDto bookingDto);
    Booking findById(Long id, Long userId);
    List<Booking> findAllByIdUser(Long userId, String state);
    List<Booking> findAllByIdOwner(Long userId, String state);
    Booking findByState(String state, Long userId);

    Booking approvedStatusOfItem(Long userId, Long bookingId, Boolean approved);
}
