package ru.practicum.shareit.requests;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

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
    public ItemRequestDto safe(Long userId, ItemRequestDto itemRequestDto) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRequestDto.getDescription().isEmpty() || itemRequestDto.getDescription() == null){
            throw new NotFoundException("Отсутсвует описание в запросе!");
        }
        @Valid ItemRequest itemRequest = ItemRequestMapping.toItemRequest(itemRequestDto, userRepository.findById(userId).get());
        itemRequest.setRequestor(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapping.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findRequestByUserId(Long userId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        return itemRequestRepository.findByRequestorId(userId).stream()
//                .map((ItemRequest itemRequest) -> ItemRequestMapping.toItemRequestDto(itemRequest))
                .map(ItemRequestMapping::toItemRequestDto)
                .collect(Collectors.toList());
    }

    //Для реализации пагинации используйте возможности, предоставляемые JpaRepository . Вам нужно определить в
    //интерфейсе репозитория метод поиска, аналогичный тому, который вы использовали ранее, но принимающий в
    //качестве параметра также объект Pageable . Например, для поиска вещи ранее использовался метод List<Item>
    //findByOwnerId , создайте метод Page<Item> findByOwnerId(Long ownerId, Pageable pageable) . Тогда всё остальное для
    //реализации пагинации на уровне базы данных для вас сделает Spring.
    //Вам нужно будет только изменить вызов к данному методу, передавая в качестве дополнительного параметра
    //описание требуемой страницы. Для этого используйте метод PageRequest.of(page, size, sort) . Обратите внимание,
    //что вам нужно будет преобразовать параметры, передаваемые пользователем, — start и size — к параметрам,
    //требуемым Spring, — page и тот же size .
    @Override
    public List<ItemRequestDto> findRequestByParam(Long userId, Long from, Long size) {
        return null;
    }

    @Override
    public ItemRequestDto findById(Long userId, Long requestId) {
        if (userRepository.findById(userId).isEmpty()){
            throw new NotFoundException("Такого пользователя не существует!");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()){
            throw new NotFoundException("Такого запроса не существует!");
        }
        Item item = itemRepository.findByRequestId(requestId);
        return ItemRequestMapping.toItemRequestDto(itemRequestRepository.findById(requestId).get(), ItemMapping.toItemDtoForRequest(item, userId));
    }
}
