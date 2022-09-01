package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper mapper;
    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, ItemMapper mapper, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
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
        Item item = ItemMapping.toItem(itemDto, userService.findById(userId));
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
        return itemRepository.findByOwnerId(userId);
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
            throw new NotFoundException("Комментарий отсутсвует!");
        }
        List<Booking> booking = bookingRepository.findByItemId(itemId);
        if (booking.isEmpty()){
            throw new NotFoundException("Данный пользователь не бронировал вещь");
        }
        //пробежаться по списку и проверить завершенные
        comment.setItem(itemRepository.findById(itemId).get());
        comment.setAuthor(userService.findById(userId));
        commentRepository.save(comment);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentByIdItem(Long itemId){
        return commentRepository.findByItemId(itemId);
    }
}

