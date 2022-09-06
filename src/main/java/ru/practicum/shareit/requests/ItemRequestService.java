package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.ItemDtoForRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto safe(Long userId, ItemRequestDto itemRequestDto);
    List<ItemRequestDto> findRequestByUserId(Long userId);
    List<ItemRequestDto> findRequestByParam(Long userId, Long from, Long size);
    ItemRequestDto findById(Long userId, Long requestId);
}
