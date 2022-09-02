package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;


    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * X-Sharer-User-Id - это собственник вещи
     */
    @PostMapping
    public ItemDto create(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту /items. Метод POST");
        return ItemMapping.toItemDto(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{id}")
    public ItemDto updateById(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody ItemDto itemDto){
        log.info("Получен запрос к эндпоинту /items. Метод PATCH");
        return ItemMapping.toItemDto(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping
//    public List<ItemDtoWithComments> allItems(@RequestHeader(value="X-Sharer-User-Id") Long userId) {
    public List<ItemDto> allItems(@RequestHeader(value="X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск всех вещей");
        return itemService.findAllItems(userId).stream()
                .map(ItemMapping::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDtoWithComments itemById(@RequestHeader(value="X-Sharer-User-Id") Long userId,
//    public ItemDto itemById(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по ID");
//        Item item = itemService.findByUserIdAndItemId(userId, itemId);
//        List<Comment> comment = itemService.getCommentByIdItem(itemId);
//        ItemDtoWithComments itemDto = ItemMapping.toItemDtoWithComments(item, comment);
//        return ItemMapping.toItemDto(itemService.findByUserIdAndItemId(userId, itemId));
        return ItemMapping.toItemDtoWithComments(userId,
                                                itemService.findByUserIdAndItemId(userId, itemId),
                                                itemService.getCommentByIdItem(itemId),
                                                itemService.getBookingByIdItem(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> itemByText(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                                    @RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по тексту");
        return itemService.findByText(userId, text).stream()
                .map(ItemMapping::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/comment")
    public Comment addComment(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody Comment comment){
        log.info("Получен запрос к эндпоинту /items/{id}/comment. Метод Post. Добавление комментария");
        return itemService.addCommentToItem(userId, itemId, comment);
    }

}
