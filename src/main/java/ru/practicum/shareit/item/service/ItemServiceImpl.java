package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoUserInHeaderException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    public ItemServiceImpl(ItemDao itemDao, UserDao userDao) {
        this.itemDao = itemDao;
        this.userDao = userDao;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userDao.containsUserById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        Item item = ItemMapper.toItem(itemDto, userDao.findUserById(userId));
        return ItemMapper.toItemDto(itemDao.create(item));



    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId == null) {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }
        if (!userDao.containsUserById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (!itemDao.containsItemById(itemId)) {
            throw new NotFoundException("Такой вещи не существует!");
        }
        if (!itemDao.findItemById(itemId).getOwner().getUserId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем данной вещи!");
        }
        Item item = ItemMapper.toItem(itemDto, userDao.findUserById(userId));
        return ItemMapper.toItemDto(itemDao.updateById(itemId, item));
    }

    @Override
    public List<ItemDto> findAllItems(Long userId) {
        return itemDao.findAllItems().stream()
                .filter(item -> item.getOwner().getUserId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        if (!userDao.containsUserById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        ItemDto getItemDto = itemDao.findAllItems().stream()
                .filter(item -> item.getItemId().equals(itemId))
                .findAny()
                .map(ItemMapper::toItemDto)
                .orElse(null);
        if (getItemDto == null){
            throw new NotFoundException("Такой вещи у пользователя с id " + userId + " нет!");
        }
        return getItemDto;

    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        if (!userDao.containsUserById(userId)) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (text == null || text.isEmpty()){
            return new ArrayList<>();
        } else {
            return itemDao.findAllItems().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

    }
}

