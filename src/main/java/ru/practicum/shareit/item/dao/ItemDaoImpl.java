package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class ItemDaoImpl implements ItemDao {
    private Long itemId = 1L;
    private Map<Long, Item> mapItems = new HashMap<>();

    @Override
    public Item create(Item item) {
        item.setId(itemId++);
        mapItems.put(item.getId(), item);
        return mapItems.get(item.getId());
    }

    @Override
    public Item updateById(Long itemId, Item item) {
        if (mapItems.containsKey(itemId)){
            Item getItem = mapItems.get(itemId);
            if (item.getName() != null){
                getItem.setName(item.getName());
            }
            if (item.getDescription() != null){
                getItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null){
                getItem.setAvailable(item.getAvailable());
            }
            return mapItems.get(itemId);
        } else {
            throw new NotFoundException("Нет такой вещи c ID " + itemId);
        }
    }

    @Override
    public Item findItemById(Long itemId) {
        if (mapItems.containsKey(itemId)){
            return mapItems.get(itemId);
        } else {
            throw new NotFoundException("Нет такой вещи c ID " + itemId);
        }
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(mapItems.values());
    }

    @Override
    public boolean deleteItemById(Long itemId) {
        if (mapItems.containsKey(itemId)){
            mapItems.remove(itemId);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsItemById(Long itemId) {
        return mapItems.containsKey(itemId);
    }
}
