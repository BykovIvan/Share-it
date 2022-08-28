package ru.practicum.shareit.item;

import java.util.List;

/**
 * Класс отвечающий за логику работы бронирования
 * The class responsible for the logic of the booking operation
 */

public interface ItemService {

    /**
     * Проверяет наличие человека, который создает, сначала в запросе,
     * затем наличие его в хранилеще, затем создает вещь
     * Checks for the existence of the person who creates, first in the request,
     * then having it in the store then creates the thing
     */
    ItemDto create(Long userId, ItemDto itemDto);

    /**
     * Метод как для полного обновления вещи, так и для частичного,
     * так же проверяет наличие id пользователя в запросе и в хранилище
     * обновлять может только владелец вещи
     * A method for both a complete update of a thing, and for a partial
     * also checks for the presence of the user id in the request and in the store
     * only the owner of the item can update
     */
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    /**
     * находит все вещи пользователя, id пользователя в запросе
     * finds all the things of the user, the user id in the request
     */
    List<ItemDto> findAllItems(Long userId);

    /**
     * находит вещь пользователя по id вещи и id пользователя в запросе
     * finds a user item by item id and user id in the request
     */
    ItemDto findById(Long userId, Long itemId);

    /**
     * поиск вещи по слову в пути запроса
     * search for a thing by a word in the query path
     */
    List<ItemDto> findByText(Long userId, String text);
}