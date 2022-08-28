package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking save(Booking booking){
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeStatusBookingById(Long bookingId, Long userId, Boolean status) {
        return null;
    }

    @Override
    public Booking findById(Long id, Long userId) {
        return null;
    }

    @Override
    public List<Booking> findAllByIdUser(Long userId, String state) {
        return null;
    }

    @Override
    public List<Booking> findAllByIdOwner(Long userId, String state) {
        return null;
    }
}
