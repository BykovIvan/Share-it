package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
                              @Valid @RequestBody ItemRequestDto itemRequestDto){
        log.info("Получен запрос к эндпоинту /requests. Метод POST");
        return itemRequestService.safe(userId, itemRequestDto);
    }

    @GetMapping
    public ItemRequestDto findRequestByUserId(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId){

        return null;
    }


}
