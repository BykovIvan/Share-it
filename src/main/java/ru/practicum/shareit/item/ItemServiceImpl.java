package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapping;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

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
    private final ItemMapper mapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, ItemMapper mapper, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapping.toItem(itemDto, userRepository.findById(userId).get());
        return ItemMapping.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (!itemRepository.findById(itemId).get().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }

        if (itemRepository.findById(itemId).isPresent()) {
            Item item = itemRepository.findById(itemId).get();
            mapper.updateItemFromDto(itemDto, item);
            itemRepository.save(item);
            return ItemMapping.toItemDto(itemRepository.findById(itemId).get());
        } else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public List<ItemDtoWithComments> findAllItems(Long userId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        return itemRepository.findByOwnerId(userId, Sort.by(Sort.Direction.ASC, "id")).stream()
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

    @Override
    public Item findById(Long itemId){
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()){
            throw new NotFoundException("Такой вещи не найдено");
        }
        return item.get();
    }

    @Override
    public ItemDtoWithComments findByUserIdAndItemId(Long userId, Long itemId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
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
    public List<ItemDto> findByText(Long userId, String text) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.search(text).stream()
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
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (commentDto.getText() == null || commentDto.getText().isEmpty()) {
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
        Comment comment = CommentMapping.toComment(commentDto, itemRepository.findById(itemId).get(), userRepository.findById(userId).get());
        comment.setCreated(new Timestamp(System.currentTimeMillis()));
        return CommentMapping.toCommentDto(commentRepository.save(comment));
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

