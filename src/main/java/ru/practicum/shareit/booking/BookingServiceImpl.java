package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapping;
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

    public Booking create(Long userId, BookingDto bookingDto){
        if (!userService.containsById(userId)){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemService.containsById(bookingDto.getItemId())){
            throw new NotFoundException("Такой вещи не существует!");
        }
        Item item = itemService.findById(userId, bookingDto.getItemId());
        User booker = userService.findById(userId);
        User owner = userService.findById(item.getOwner().getUserId());
        if (booker.getUserId().equals(owner.getUserId())){
            throw new BadRequestException("Владелец не может забронировать свою вещь!");
        }
        if (!item.getAvailable()){
            throw new BadRequestException("Вещь не доступна!");
        }

        Booking booking = BookingMapping.toBooking(bookingDto, booker, item);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeStatusBookingById(Long bookingId, Long userId, Boolean status) {

        return null;
    }

    @Override
    public Booking findById(Long id, Long userId) {
        //Сделать проверку получения, получать может только владелец или орендатор
        Optional<Booking> bookingGet = bookingRepository.findById(id);
        if (bookingGet.isPresent()){
            Booking booking = bookingGet.get();
            if (booking.getBooker().getUserId().equals(id) || booking.getItem().getOwner().getUserId().equals(id)){
                return bookingGet.get();
            } else {
                throw new NotFoundException("Пользователь не является владельцем или арендатором вещи");
            }
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
