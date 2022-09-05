package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapping;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    private final UserService userService;


    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

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
    public List<ItemDtoWithComments> allItems(@RequestHeader(value="X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск всех вещей");
        return itemService.findAllItems(userId).stream()
                .map((Item item) -> ItemMapping.toItemDtoWithComments(userId,
                        itemService.findByUserIdAndItemIdAll(userId, item.getId()),
                        itemService.getCommentByIdItem(item.getId()).stream()
                                .map(CommentMapping::toCommentDto)
                                .collect(Collectors.toList()),
                        itemService.getBookingByIdItem(item.getId()).stream()
                                .map(BookingMapping::toBookingDto)
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDtoWithComments itemById(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                            @PathVariable("id") Long itemId) {
        log.info("Получен запрос к эндпоинту /items. Метод GET. Поиск по ID");
        return ItemMapping.toItemDtoWithComments(userId,
                                                itemService.findByUserIdAndItemId(userId, itemId),
                                                itemService.getCommentByIdItem(itemId).stream()
                                                        .map(CommentMapping::toCommentDto)
                                                        .collect(Collectors.toList()),
                                                itemService.getBookingByIdItem(itemId).stream()
                                                        .map(BookingMapping::toBookingDto)
                                                        .collect(Collectors.toList()));
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
    public CommentDto addComment(@RequestHeader(value="X-Sharer-User-Id") Long userId,
                              @PathVariable("id") Long itemId,
                              @RequestBody CommentDto commentDto){
        log.info("Получен запрос к эндпоинту /items/{id}/comment. Метод Post. Добавление комментария");
        return CommentMapping.toCommentDto(itemService.addCommentToItem(userId, itemId, CommentMapping.toComment(commentDto, itemService.findById(itemId), userService.findById(userId))));
    }

}
