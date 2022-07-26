package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    /**
     * Добавление вещи в локальное хранилище
     * Adding the Item to local storage
     */
    Item create(Item item);

    /**
     * Обновление вещи в локальном хранилище
     * Update the item in local storage
     */
    Item updateById(Long itemId, Item item);

    /**
     * Поиск вещи по Id
     * Get item by ID
     */
    Item findItemById(Long itemId);

    /**
     * Получение всех вещей из хранилища
     * Getting all items from local storage
     */
    List<Item> findAllItems();

    /**
     * Удаление вещи из локального хранилища
     * Remove the item from local storage
     */
    boolean deleteItemById(Long itemId);

    /**
     * Провека есть ли вещи в локальном хранилище
     * Check item in local storage
     */
    boolean containsItemById(Long itemId);
}

