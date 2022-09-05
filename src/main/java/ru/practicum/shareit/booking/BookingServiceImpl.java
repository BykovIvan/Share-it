package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
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


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        LocalDateTime startDay = bookingDto.getStart();
        LocalDateTime endDay = bookingDto.getEnd();
        LocalDateTime nowDate = LocalDateTime.now();
        if (startDay.isBefore(nowDate)){
            throw new BadRequestException("Время начала не может быть в прошлом!");
        }
        if (endDay.isBefore(nowDate)) {
            throw new BadRequestException("Время окончания не может быть в прошлом!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания не может быть раньше начала бронирования!");
        }
        Item item = itemService.findById(bookingDto.getItemId());
        User booker = userService.findById(userId);
        User owner = userService.findById(item.getOwner().getId());
        if (booker.getId().equals(owner.getId())) {
            throw new NotFoundException("Владелец не может забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна!");
        }
        List<Booking> listOfItemsById = bookingRepository.findByItemId(item.getId(), Sort.by("start"));

        for (Booking booking : listOfItemsById) {
            if ((bookingDto.getStart().isBefore(booking.getStart().toLocalDateTime()) && bookingDto.getEnd().isBefore(booking.getStart().toLocalDateTime()))
                || (bookingDto.getStart().isAfter(booking.getEnd().toLocalDateTime()) && bookingDto.getEnd().isAfter(booking.getEnd().toLocalDateTime()))) {

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
        if (approved && booking.getStatus().equals(StatusOfItem.APPROVED)){
            throw new BadRequestException("Статус уже подтвержден!");
        }
        if (!approved && booking.getStatus().equals(StatusOfItem.REJECTED)){
            throw new BadRequestException("Статус уже не подтвержден!");
        }
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
                throw new NotFoundException("Не является владельцем или арентадателем вещи!");
            }
        } else {
            throw new NotFoundException("Нет такого бронирования!");
        }

    }

    @Override
    public List<Booking> findBookingByUserIdAndState(String state, Long userId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "id"));
            case "CURRENT":
                return bookingRepository.findByBookerIdByUserId(userId, new Timestamp(System.currentTimeMillis()));
            case "PAST":
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"));
            case "FUTURE":
                return bookingRepository.findByBookerIdAndStartAfter(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"));
            case "WAITING":
                return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.WAITING, Sort.by(Sort.Direction.DESC, "id"));
            case "REJECTED":
                return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.REJECTED, Sort.by(Sort.Direction.DESC, "id"));
        }
        throw new NoUserInHeaderException("Unknown state: UNSUPPORTED_STATUS");
    }


    @Override
    public List<Booking> findItemByOwnerIdAndState(String state, Long userId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        switch (state) {
            case "ALL":
                return bookingRepository.searchOwnerByOwnerId(userId);
            case "CURRENT":
                return bookingRepository.searchOwnerByOwnerIdCurrent(userId, new Timestamp(System.currentTimeMillis()));
            case "PAST":
                return bookingRepository.searchOwnerByOwnerIdPast(userId, new Timestamp(System.currentTimeMillis()));
            case "FUTURE":
                return bookingRepository.searchOwnerByOwnerIdFuture(userId, new Timestamp(System.currentTimeMillis()));
            case "WAITING":
                return bookingRepository.searchOwnerByOwnerIdWaitingAndRejected(userId, StatusOfItem.WAITING);
            case "REJECTED":
                return bookingRepository.searchOwnerByOwnerIdWaitingAndRejected(userId, StatusOfItem.REJECTED);
        }
        throw new NoUserInHeaderException("Unknown state: UNSUPPORTED_STATUS");
    }
}
