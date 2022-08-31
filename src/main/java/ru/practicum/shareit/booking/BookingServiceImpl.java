package ru.practicum.shareit.booking;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.StatusOfItem;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;


    public BookingServiceImpl(BookingRepository bookingRepository, UserService userService, ItemService itemService) {
        this.bookingRepository = bookingRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    public Booking create(Long userId, BookingDto bookingDto) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemService.containsById(bookingDto.getItemId())) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        long startDay = TimeUnit.MILLISECONDS.toDays(bookingDto.getStart().getTime());
        long endDay = TimeUnit.MILLISECONDS.toDays(bookingDto.getEnd().getTime());
        long nowDate = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis());
        if (startDay < nowDate) {
            throw new BadRequestException("Время начала не может быть в прошлом!");
        }
        if (endDay < nowDate) {
            throw new BadRequestException("Время окончания не может быть в прошлом!");
        }
        if (bookingDto.getEnd().before(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания не может быть раньше начала бронирования!");
        }
        Item item = itemService.findById(userId, bookingDto.getItemId());
        User booker = userService.findById(userId);
        User owner = userService.findById(item.getOwner().getId());
        if (booker.getId().equals(owner.getId())) {
            throw new BadRequestException("Владелец не может забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна!");
        }
        List<Booking> listOfItemsById = bookingRepository.findByItemId(item.getId());

        for (Booking booking : listOfItemsById) {
            if ((bookingDto.getStart().before(booking.getStart()) && bookingDto.getEnd().before(booking.getStart()))
                || (bookingDto.getStart().after(booking.getEnd()) && bookingDto.getEnd().after(booking.getEnd()))) {
            } else {
                throw new BadRequestException("Вещь в данный переод времени забронирована!");
            }
        }
        Booking booking = BookingMapping.toBooking(bookingDto, booker, item);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approvedStatusOfItem(Long userId, Long bookingId, Boolean approved) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (!bookingOptional.isPresent()) {
            throw new NotFoundException("Такое бронирование не найдено!");
        }
        Booking booking = bookingOptional.get();
        Item item = bookingOptional.get().getItem();
        User owner = item.getOwner();
        if (!owner.getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи!");
        }
        if (approved) {
            itemService.update(userId, item.getId(), ItemMapping.toItemDto(item));
            booking.setStatus(StatusOfItem.APPROVED);
            bookingRepository.save(booking);
        } else {
            booking.setStatus(StatusOfItem.REJECTED);
            bookingRepository.save(booking);
        }
        return booking;
    }

    @Override
    public Booking findById(Long id, Long userId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (id == null){
            throw new NoUserInHeaderException("Отсутсвует id бронирования в запросе!");
        }
        Optional<Booking> bookingGet = bookingRepository.findById(id);
        if (bookingGet.isPresent()) {
            Booking booking = bookingGet.get();
            if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)){
                return booking;
            }else {
                throw new NoUserInHeaderException("Не является владельцем или арентадателем вещи!");
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

    @Override
    public Booking findByState(String state, Long userId) {
        switch (state) {
            case "ALL":
                //сформировать запросы для всех и тп.
                break;
            case "CURRENT":

                break;
            case "PAST":

                break;
            case "FUTURE":

                break;
            case "REJECTED":

                break;
        }
        return null;
    }
}
