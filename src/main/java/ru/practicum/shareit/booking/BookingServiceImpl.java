package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public Booking create(Long userId, Booking booking){
        if (!userService.containsById(userId)){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemService.containsById(booking.getItem().getId())){
            throw new NotFoundException("Такой вещи не существует!");
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeStatusBookingById(Long bookingId, Long userId, Boolean status) {
        return null;
    }

    @Override
    public Booking findById(Long id, Long userId) {
        //Сделать проверку получения, получать может только владелец или орендатор
        Optional<Booking> bookingGet =  bookingRepository.findById(id);
        if (bookingGet.isPresent()){
            return bookingGet.get();
        } else {
            throw new NotFoundException("Нет такого бронирования!");
        }
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
