package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapping;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.FromSizeSortPageable;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        Item item;
        if (itemDto.getRequestId() == null) {
            item = ItemMapping.toItem(itemDto, userRepository.findById(userId).get());
        } else {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() -> new NotFoundException("Такого запроса не существует!"));
            item = ItemMapping.toItem(itemDto, itemRequest, user);
        }
        return ItemMapping.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи не существует!"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapping.toItemDto(itemRepository.findById(itemId).get());

    }

    @Override
    public List<ItemDtoWithComments> findAllItems(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (from == null || size == null) {
            return itemRepository.findByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id"))
                    .stream()
                    .map((Item item) -> ItemMapping.toItemDtoWithComments(userId,
                            findByUserIdAndItemIdAll(userId, item.getId()),
                            getCommentByIdItem(item.getId()).stream()
                                    .map(CommentMapping::toCommentDto)
                                    .collect(Collectors.toList()),
                            getBookingByIdItem(item.getId()).stream()
                                    .map(BookingMapping::toBookingDto)
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } else {
            if (from < 0 || size <= 0) {
                throw new BadRequestException("Введены неверные параметры!");
            }
            return itemRepository.findByOwnerId(userId, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.ASC, "id")))
                    .stream()
                    .map((Item item) -> ItemMapping.toItemDtoWithComments(userId,
                            findByUserIdAndItemIdAll(userId, item.getId()),
                            getCommentByIdItem(item.getId()).stream()
                                    .map(CommentMapping::toCommentDto)
                                    .collect(Collectors.toList()),
                            getBookingByIdItem(item.getId()).stream()
                                    .map(BookingMapping::toBookingDto)
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи не существует!"));
    }

    @Override
    public ItemDtoWithComments findByUserIdAndItemId(Long userId, Long itemId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        Item item = itemRepository.findByIdAndAvailable(itemId, true);
        if (item == null) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        return ItemMapping.toItemDtoWithComments(userId,
                item,
                getCommentByIdItem(itemId).stream()
                        .map(CommentMapping::toCommentDto)
                        .collect(Collectors.toList()),
                getBookingByIdItem(itemId).stream()
                        .map(BookingMapping::toBookingDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public Item findByUserIdAndItemIdAll(Long userId, Long itemId) {
        return itemRepository.findByIdAndOwnerId(itemId, userId);
    }

    @Override
    public List<ItemDto> findByText(Long userId, String text, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else if (from == null || size == null) {
            return itemRepository.search(text)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapping::toItemDto)
                    .collect(Collectors.toList());
        } else {
            if (from < 0 || size <= 0) {
                throw new BadRequestException("Введены неверные параметры!");
            }
            return itemRepository.searchWithPageable(text, FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.ASC, "id")))
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapping::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean containsById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent();
    }

    @Override
    public CommentDto addCommentToItem(Long userId, Long itemId, CommentDto commentDto) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Такой вещи не существует!"));
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
            throw new BadRequestException("Комментарий отсутсвует!");
        }
        List<Booking> booking = bookingRepository.findByItemIdAndBookerId(itemId, userId, Sort.by("start"));
        if (booking.isEmpty()) {
            throw new NotFoundException("Бронирование данной вещи не существует!");
        }
        for (Booking bookingGet : booking) {
            if (bookingGet.getEnd().toLocalDateTime().isBefore(LocalDateTime.now())) {
                break;
            } else {
                throw new BadRequestException("Пока ни одного бронирования не завершено!");
            }
        }
        Comment comment = CommentMapping.toComment(commentDto, itemRepository.findById(itemId).get(), userRepository.findById(userId).get());
        comment.setCreated(new Timestamp(System.currentTimeMillis()));
        return CommentMapping.toCommentDto(commentRepository.save(comment));
    }


    @Override
    public List<Comment> getCommentByIdItem(Long itemId) {
        return commentRepository.findAllByItemId(itemId);
    }

    @Override
    public List<Booking> getBookingByIdItem(Long itemId) {
        return bookingRepository.findByItemId(itemId, Sort.by("start"));
    }
}

