package ru.practicum.shareit.item.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Создание предмета
     * Create item
     */
    @PostMapping
    public ItemDto create(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту /items. Метод POST");
        return itemService.create(userId, itemDto);
    }

    /**
     * Обновление вещи по ID и body
     * Update item by ID and body
     */
    @PatchMapping("/{id}")
    public ItemDto updateById(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody ItemDto itemDto){
        log.info("Получен запрос к эндпоинту /items. Метод PATCH");
        return itemService.update(userId, itemId, itemDto);
    }

    /**
     * Получение списка всех вещей
     * Get list of items
     */
    @GetMapping
    public List<ItemDto> allItems(@RequestHeader(value="X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск всех вещей");
        return itemService.findAllItems(userId);
    }

    /**
     * Получение вещи по его id и по id пользователю
     * Get item by ID and id of user
     */
    @GetMapping("/{id}")
    public ItemDto itemById(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по ID");
        return itemService.findById(userId, itemId);
    }

    /**
     * Поиск вещи по тексту
     * Search item by text
     */
    @GetMapping("/search")
    public List<ItemDto> itemByText(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                                    @RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по тексту");
        return itemService.findByText(userId, text);
    }
}
