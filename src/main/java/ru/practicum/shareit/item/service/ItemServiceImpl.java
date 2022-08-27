package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(userId).get());
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (!itemRepository.findById(itemId).get().getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }
        Item item = ItemMapper.toItem(itemDto, userRepository.findById(userId).get());
        //TODO save with id!!!!
        return ItemMapper.toItemDto(itemRepository.save(item));
//        return ItemMapper.toItemDto(itemRepository.updateById(itemId, item));
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getUserId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        ItemDto getItemDto = itemRepository.findAll().stream()
                .filter(item -> item.getItemId().equals(itemId))
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
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findAll().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

    }
}

