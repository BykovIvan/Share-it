package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    Booking create(Long userId, BookingDto bookingDto);
    Booking changeStatusBookingById(Long bookingId, Long userId,  Boolean status);
    Booking findById(Long id, Long userId);
    List<Booking> findAllByIdUser(Long userId, String state);
    List<Booking> findAllByIdOwner(Long userId, String state);
    Booking findByState(String state, Long userId);
}
