package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.FromSizeSortPageable;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            throw new BadRequestException("Отсутсвует описание в запросе!");
        }
        @Valid ItemRequest itemRequest = ItemRequestMapping.toItemRequest(itemRequestDto, user);
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapping.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findRequestByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        return itemRequestRepository.findByRequestorId(userId)
                .stream()
                .map((ItemRequest itemRequest) -> ItemRequestMapping.toItemRequestDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId())
                        .stream()
                        .map((Item item) -> ItemMapping.toItemDtoForRequest(item, itemRequest.getRequestor().getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findRequestByParam(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        if (from == null || size == null) {
            return itemRequestRepository.findAll()
                    .stream()
                    .filter((ItemRequest itemRequest) -> !itemRequest.getId().equals(userId))
                    .map((ItemRequest itemRequest) -> ItemRequestMapping.toItemRequestDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId())
                            .stream()
                            .map((Item item) -> ItemMapping.toItemDtoForRequest(item, itemRequest.getRequestor().getId()))
                            .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        }
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Введены неверные параметры!");
        }
        return itemRequestRepository.findAll(FromSizeSortPageable.of(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .filter((ItemRequest itemRequest) -> !itemRequest.getId().equals(userId))
                .map((ItemRequest itemRequest) -> ItemRequestMapping.toItemRequestDto(itemRequest, itemRepository.findByRequestId(itemRequest.getId())
                        .stream()
                        .map((Item item) -> ItemMapping.toItemDtoForRequest(item, itemRequest.getRequestor().getId()))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя не существует!"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Такого запроса не существует!"));
        List<ItemDtoForRequest> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map((Item item) -> ItemMapping.toItemDtoForRequest(item, itemRequest.getRequestor().getId()))
                .collect(Collectors.toList());
        return ItemRequestMapping.toItemRequestDto(itemRequest, items);
    }
}
