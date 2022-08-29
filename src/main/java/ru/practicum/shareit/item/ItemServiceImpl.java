package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapping;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
//    private final ItemDao itemRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper mapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapping.toItem(itemDto, userService.findById(userId));
        return ItemMapping.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
//        if (itemRepository.containsItemById(itemId)) {
        if (!itemRepository.findById(itemId).isPresent()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
//        if (!itemRepository.findItemById(itemId).getOwner().getUserId().equals(userId)) {
        if (!itemRepository.findById(itemId).get().getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }
//        Item item = ItemMapping.toItem(itemDto, userService.findById(userId));
//        item.setId(itemId);

        if (itemRepository.findById(itemId).isPresent()){
            Item item = itemRepository.findById(itemId).get();
            mapper.updateItemFromDto(itemDto, item);
            itemRepository.save(item);
            return ItemMapping.toItemDto(itemRepository.findById(itemId).get());
        }else {
            throw new NotFoundException("Такого пользователя не существует!");
        }


//        return ItemMapper.toItemDto(itemRepository.create(item));
//        return ItemMapping.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getUserId().equals(userId))
                .map(ItemMapping::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        ItemDto getItemDto = itemRepository.findAll().stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .map(ItemMapping::toItemDto)
                .orElse(null);
        if (getItemDto == null) {
            throw new NotFoundException("Такой вещи у пользователя с id " + userId + " нет!");
        }
        return getItemDto;

    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findAll().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(ItemMapping::toItemDto)
                    .collect(Collectors.toList());
        }

    }

    @Override
    public boolean containsById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        return item.isPresent();
    }
}

