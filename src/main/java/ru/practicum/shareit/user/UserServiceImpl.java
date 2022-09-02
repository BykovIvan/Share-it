package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;


    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public User update(Long id, UserDto userDto) {
        if (repository.findById(id).isPresent()){
            User user = repository.findById(id).get();
            mapper.updateUserFromDto(userDto, user);
            repository.save(user);
            return repository.findById(id).get();
        }else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public User findById(Long id) {
        Optional<User> userGet =  repository.findById(id);
        if (userGet.isPresent()){
            return userGet.get();
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
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
