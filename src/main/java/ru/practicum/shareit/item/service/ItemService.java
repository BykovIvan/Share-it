package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * Класс отвечающий за логику работы бронирования
 * The class responsible for the logic of the booking operation
 */

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    List<ItemDto> findAllItems(Long userId);

    ItemDto findById(Long userId, Long itemId);

    List<ItemDto> findByText(Long userId, String text);
}