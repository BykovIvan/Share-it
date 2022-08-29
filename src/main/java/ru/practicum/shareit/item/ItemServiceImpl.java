package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
//    private final ItemDao itemRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapper.toItem(itemDto, userService.findById(userId));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
//        if (itemRepository.containsItemById(itemId)) {
        if (itemRepository.findById(itemId).isPresent()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
//        if (!itemRepository.findItemById(itemId).getOwner().getUserId().equals(userId)) {
        if (!itemRepository.findById(itemId).get().getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }
        Item item = ItemMapper.toItem(itemDto, userService.findById(userId));
        item.setId(itemId);
        //TODO save with id!!!!
//        return ItemMapper.toItemDto(itemRepository.create(item));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return itemRepository.findAll().stream()
//        return itemRepository.findAllItems().stream()
                .filter(item -> item.getOwner().getUserId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        if (userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        ItemDto getItemDto = itemRepository.findAll().stream()
//        ItemDto getItemDto = itemRepository.findAllItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .map(ItemMapper::toItemDto)
                .orElse(null);
        if (getItemDto == null) {
            throw new NotFoundException("Такой вещи у пользователя с id " + userId + " нет!");
        }
        return getItemDto;

    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        if (userService.containsById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findAll().stream()
//            return itemRepository.findAllItems().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

    }
}

