package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.ItemDtoForRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto safe(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestDto> findRequestByUserId(Long userId);
    List<ItemRequestDto> findRequestByParam(Long userId, int from, int size);
    ItemRequestDto findById(Long userId, Long requestId);
}
