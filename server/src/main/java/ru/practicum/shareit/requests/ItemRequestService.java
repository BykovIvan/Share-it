package ru.practicum.shareit.requests;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findRequestByUserId(Long userId);

    List<ItemRequestDto> findRequestByParam(Long userId, Integer from, Integer size);

    ItemRequestDto findById(Long userId, Long requestId);
}
