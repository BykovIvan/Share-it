package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

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

    @PostMapping
    public ItemDto create(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос к эндпоинту /items. Метод POST");
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateById(@RequestHeader(value="X-Sharer-User-Id", required = false) Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody ItemDto itemDto){
        log.info("Получен запрос к эндпоинту /items. Метод PATCH");
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDtoWithComments> allItems(@RequestHeader(value="X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск всех вещей");
        return itemService.findAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDtoWithComments itemById(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по ID");
        return itemService.findByUserIdAndItemId(userId, itemId);

    }

    @GetMapping("/search")
    public List<ItemDto> itemByText(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                                    @RequestParam("text") String text) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по тексту");
        return itemService.findByText(userId, text);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody CommentDto commentDto){
        log.info("Получен запрос к эндпоинту /items/{id}/comment. Метод Post. Добавление комментария");
        return itemService.addCommentToItem(userId, itemId, commentDto);
    }

}
