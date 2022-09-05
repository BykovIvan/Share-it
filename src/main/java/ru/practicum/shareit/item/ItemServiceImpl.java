package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemMapper mapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, UserService userService, ItemMapper mapper, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.mapper = mapper;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Item create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapping.toItem(itemDto, userRepository.findById(userId).get());
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemRepository.findById(itemId).isPresent()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }

        if (itemRepository.findById(itemId).isPresent()) {
            Item item = itemRepository.findById(itemId).get();
            mapper.updateItemFromDto(itemDto, item);
            itemRepository.save(item);
            return itemRepository.findById(itemId).get();
        } else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        return itemRepository.findByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id"));
    }

    @Override
    public Item findById(Long itemId){
        Optional<Item> item = itemRepository.findById(itemId);
        if (!item.isPresent()){
            throw new NotFoundException("Такой вещи не найдено");
        }
        return item.get();
    }

    @Override
    public Item findByUserIdAndItemId(Long userId, Long itemId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = itemRepository.findByIdAndAvailable(itemId, true);
        if (item == null) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        return item;
    }

    @Override
    public Item findByUserIdAndItemIdAll(Long userId, Long itemId) {
        return itemRepository.findByIdAndOwnerId(itemId, userId);
    }

    @Override
    public List<Item> findByText(Long userId, String text) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.search(text).stream()
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public boolean containsById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent();
    }

    @Override
    public Comment addCommentToItem(Long userId, Long itemId, Comment comment) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemRepository.findById(itemId).isPresent()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (comment.getText() == null || comment.getText().isEmpty()) {
            throw new BadRequestException("Комментарий отсутсвует!");
        }
        List<Booking> booking = bookingRepository.findByItemIdAndBookerId(itemId, userId, Sort.by("start"));
        if (booking.isEmpty()){
            throw new NotFoundException("Бронирование данной вещи не существует!");
        }
        for (Booking bookingGet : booking) {
            if (bookingGet.getEnd().toLocalDateTime().isBefore(LocalDateTime.now())){
                break;
            }else {
                throw new BadRequestException("Пока ни одного бронирования не завершено!");
            }
        }
        comment.setItem(itemRepository.findById(itemId).get());
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setCreated(new Timestamp(System.currentTimeMillis()));
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentByIdItem(Long itemId){
        return commentRepository.findAllByItemId(itemId);
    }

    @Override
    public List<Booking> getBookingByIdItem(Long itemId){
        return bookingRepository.findByItemId(itemId, Sort.by("start"));
    }
}

