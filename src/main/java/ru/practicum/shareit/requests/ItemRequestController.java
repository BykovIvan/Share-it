package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto create(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту /requests. Метод POST");
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> findRequestByUserId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId) {
        log.info("Получен запрос к эндпоинту /requests. Метод GET ALL by UserId");
        return itemRequestService.findRequestByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findByParam(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                            @RequestParam(value = "from", required = false) Integer from,
                                            @RequestParam(value = "size", required = false) Integer size) {
        log.info("Получен запрос к эндпоинту /requests. Метод GET ALL by Param");
        return itemRequestService.findRequestByParam(userId, from, size);
    }

    //Могут просматривать все пользователи
    @GetMapping("/{id}")
    public ItemRequestDto findById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
                                   @PathVariable("id") Long requestId) {
        log.info("Получен запрос к эндпоинту /requests. Метод GET by requestId");
        return itemRequestService.findById(userId, requestId);
    }


}
