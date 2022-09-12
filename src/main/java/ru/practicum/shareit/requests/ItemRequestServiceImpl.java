package ru.practicum.shareit.requests;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.utils.FromSizeSortPageable;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRepository itemRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRequestDto.getDescription().isEmpty() || itemRequestDto.getDescription() == null) {
            throw new NotFoundException("Отсутсвует описание в запросе!");
        }
        @Valid ItemRequest itemRequest = ItemRequestMapping.toItemRequest(itemRequestDto, userRepository.findById(userId).get());
        itemRequest.setRequestor(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapping.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findRequestByUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
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
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
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
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Такого запроса не существует!");
        }
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        List<ItemDtoForRequest> items = itemRepository.findByRequestId(requestId)
                .stream()
                .map((Item item) -> ItemMapping.toItemDtoForRequest(item, itemRequest.getRequestor().getId()))
                .collect(Collectors.toList());
        return ItemRequestMapping.toItemRequestDto(itemRequest, items);
    }
}
