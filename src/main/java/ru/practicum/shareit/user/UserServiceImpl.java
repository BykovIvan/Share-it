package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;


    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserDto save(User user) {
        return UserMapping.toUserDto(repository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        if (repository.findById(id).isPresent()){
            User user = repository.findById(id).get();
            mapper.updateUserFromDto(userDto, user);
            repository.save(user);
            return UserMapping.toUserDto(repository.findById(id).get());
        }else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> userGet =  repository.findById(id);
        if (userGet.isPresent()){
            return UserMapping.toUserDto(userGet.get());
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public List<UserDto> findAll() {
        return repository.findAll().stream()
                .map(UserMapping::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        if (containsById(userId)) {
            repository.deleteById(userId);
        } else {
            throw new NotFoundException("Нет такого пользователя c ID = " + userId);
        }
    }

    @Override
    public boolean containsById(Long userId) {
        Optional<User> user = repository.findById(userId);
        return user.isPresent();
    }

}
