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
        if (userId != null){
            if (userDao.containsUserById(userId)){
                Item item = ItemMapper.toItem(itemDto, userDao.findUserById(userId));
                return ItemMapper.toItemDto(itemDao.create(item));
            } else {
                throw new NotFoundException("Такого пользователя не существует!");
            }
        } else {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }

    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (userId != null){
            if (userDao.containsUserById(userId)){
                if (itemDao.containsItemById(itemId)){
                    if (itemDao.findItemById(itemId).getOwner().getUserId().equals(userId)){
                        Item item = ItemMapper.toItem(itemDto, userDao.findUserById(userId));
                        return ItemMapper.toItemDto(itemDao.updateById(itemId, item));
                    } else {
                        throw new NotFoundException("Пользователь не является владельцем данной вещи!");
                    }
                } else {
                    throw new NotFoundException("Такой вещи не существует!");
                }
            } else {
                throw new NotFoundException("Такого пользователя не существует!");
            }
        } else {
            throw new NoUserInHeaderException("В запросе отсутсвует пользователь при создании задачи!");
        }

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
        if (userDao.containsUserById(userId)){
            ItemDto getItemDto = itemDao.findAllItems().stream()
                    .filter(item -> item.getItemId().equals(itemId))
                    .findAny()
                    .map(ItemMapper::toItemDto)
                    .orElse(null);
            if (getItemDto != null){
                return getItemDto;
            } else {
                throw new NotFoundException("Такой вещи у пользователя с id " + userId + " нет!");
            }
        } else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public List<ItemDto> findByText(Long userId, String text) {
        if (userDao.containsUserById(userId)){
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
        } else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }
}

