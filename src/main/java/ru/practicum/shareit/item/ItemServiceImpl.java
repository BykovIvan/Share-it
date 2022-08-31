package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
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

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.mapper = mapper;
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

        if (itemRepository.findById(itemId).isPresent()){
            Item item = itemRepository.findById(itemId).get();
            mapper.updateItemFromDto(itemDto, item);
            itemRepository.save(item);
            return itemRepository.findById(itemId).get();
        }else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public List<Item> findAllItems(Long userId) {
        //TODO Переделать запрос в базу данных через запрос
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Item findById(Long userId, Long itemId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        //TODO Переделать запрос в базу данных через запрос
        Item getItemDto = itemRepository.findAll().stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .orElse(null);
        if (getItemDto == null) {
            throw new NotFoundException("Такой вещи у пользователя с id " + userId + " нет!");
        }
        return getItemDto;

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
            //TODO Переделать запрос в базу данных через запрос
//            return itemRepository.findAll().stream()
                    .filter(Item::getAvailable)
//                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
//                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public boolean containsById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent();
    }

    //TODO сделать метод обновления только статуса по id вещи для бронирования


}

