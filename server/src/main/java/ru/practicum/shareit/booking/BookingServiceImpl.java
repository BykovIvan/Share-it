package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.FromSizeSortPageable;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Такой вещи не существует!"));
        LocalDateTime startDay = bookingDto.getStart();
        LocalDateTime endDay = bookingDto.getEnd();
        LocalDateTime nowDate = LocalDateTime.now();
        if (startDay.isBefore(nowDate)) {
            throw new BadRequestException("Время начала не может быть в прошлом!");
        }
        if (endDay.isBefore(nowDate)) {
            throw new BadRequestException("Время окончания не может быть в прошлом!");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Время окончания не может быть раньше начала бронирования!");
        }
        User owner = userRepository.findById(item.getOwner().getId()).get();
        if (booker.getId().equals(owner.getId())) {
            throw new NotFoundException("Владелец не может забронировать свою вещь!");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь не доступна!");
        }
        List<Booking> listOfItemsById = bookingRepository.findByItemId(item.getId(), Sort.by("start"));

        for (Booking booking : listOfItemsById) {
            if (!((bookingDto.getStart().isBefore(booking.getStart().toLocalDateTime()) && bookingDto.getEnd().isBefore(booking.getStart().toLocalDateTime()))
                    || (bookingDto.getStart().isAfter(booking.getEnd().toLocalDateTime()) && bookingDto.getEnd().isAfter(booking.getEnd().toLocalDateTime())))) {
                throw new BadRequestException("Вещь в данный переод времени забронирована!");
            }
        }
        Booking booking = BookingMapping.toBooking(bookingDto, booker, item);
        return BookingMapping.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approvedStatusOfItem(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Такое бронирование не найдено!"));
        if (approved && booking.getStatus().equals(StatusOfItem.APPROVED)) {
            throw new BadRequestException("Статус уже подтвержден!");
        }
        if (!approved && booking.getStatus().equals(StatusOfItem.REJECTED)) {
            throw new BadRequestException("Статус уже не подтвержден!");
        }
        Item item = booking.getItem();
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
        return BookingMapping.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (id == null) {
            throw new NoUserInHeaderException("Отсутсвует id бронирования в запросе!");
        }
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет такого бронирования!"));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId)) {
            return BookingMapping.toBookingDto(booking);
        } else {
            throw new NotFoundException("Не является владельцем или арентадателем вещи!");
        }
    }

    @Override
    public List<BookingDto> findBookingByUserIdAndState(String state, Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new BadRequestException("Введены неверные параметры!");
            }
        }
        switch (state) {
            case "ALL":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerId(userId, Sort.by(Sort.Direction.DESC, "id")).stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerId(userId, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "CURRENT":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerIdByUserId(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerIdByUserId(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "PAST":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerIdAndEndIsBefore(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerIdAndEndIsBefore(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "FUTURE":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerIdAndStartAfter(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerIdAndStartAfter(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "WAITING":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.WAITING, Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.WAITING, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "REJECTED":
                if (from == null || size == null) {
                    return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.REJECTED, Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.findByBookerIdAndStatus(userId, StatusOfItem.REJECTED, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
        }
        throw new NoUserInHeaderException("Unknown state: UNSUPPORTED_STATUS");
    }


    @Override
    public List<BookingDto> findItemByOwnerIdAndState(String state, Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (from != null && size != null) {
            if (from < 0 || size <= 0) {
                throw new BadRequestException("Введены неверные параметры!");
            }
        }
        switch (state) {
            case "ALL":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingsByOwnerId(userId, Sort.by(Sort.Direction.DESC, "id")).stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingsByOwnerId(userId, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id"))).stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "CURRENT":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingByOwnerIdCurrent(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingByOwnerIdCurrent(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "PAST":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingsByOwnerIdPast(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingsByOwnerIdPast(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "FUTURE":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingsByOwnerIdFuture(userId, new Timestamp(System.currentTimeMillis()), Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingsByOwnerIdFuture(userId, new Timestamp(System.currentTimeMillis()), FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "WAITING":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingsByOwnerIdWaitingAndRejected(userId, StatusOfItem.WAITING, Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingsByOwnerIdWaitingAndRejected(userId, StatusOfItem.WAITING, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
            case "REJECTED":
                if (from == null || size == null) {
                    return bookingRepository.searchBookingsByOwnerIdWaitingAndRejected(userId, StatusOfItem.REJECTED, Sort.by(Sort.Direction.DESC, "id"))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                } else {
                    return bookingRepository.searchBookingsByOwnerIdWaitingAndRejected(userId, StatusOfItem.REJECTED, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "id")))
                            .stream()
                            .map(BookingMapping::toBookingDto)
                            .collect(Collectors.toList());
                }
        }
        throw new NoUserInHeaderException("Unknown state: UNSUPPORTED_STATUS");
    }
}
