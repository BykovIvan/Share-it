package ru.practicum.shareit.requests;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemDtoForRequest;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    public ItemRequestDto safe(Long userId, ItemRequestDto itemRequestDto) {
        if (!userRepository.findById(userId).isPresent()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRequestDto.getDescription().isEmpty() || itemRequestDto.getDescription() == null){
            throw new NotFoundException("Отсутсвует описание в запросе!");
        }
        ItemRequest itemRequest = ItemRequestMapping.toItemRequest(itemRequestDto, userRepository.findById(userId).get());
        itemRequest.setRequestor(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());
        return null;
//        return ItemRequestMapping.toItemRequestDto(itemRequestRepository.save(itemRequest), ItemMapping.toItemDtoForRequest());
    }

    @Override
    public List<ItemDtoForRequest> findRequestByUserId(Long userId) {
        if (!userRepository.findById(userId).isPresent()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        return null;
    }
}
