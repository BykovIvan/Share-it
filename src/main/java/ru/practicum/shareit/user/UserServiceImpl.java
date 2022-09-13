package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        @Valid User user = UserMapping.toUser(userDto);
        return UserMapping.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        if (userRepository.findById(id).isPresent()) {
            User user = userRepository.findById(id).get();
            if (userDto.getName() != null) {
                user.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                user.setEmail(userDto.getEmail());
            }
            userRepository.save(user);
            return UserMapping.toUserDto(userRepository.findById(id).get());
        } else {
            throw new NotFoundException("Такого пользователя не существует!");
        }
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> userGet = userRepository.findById(id);
        if (userGet.isPresent()) {
            return UserMapping.toUserDto(userGet.get());
        } else {
            throw new NotFoundException("Нет такого пользователя!");
        }
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(UserMapping::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Нет такого пользователя c ID = " + userId);
        }
        userRepository.deleteById(userId);
    }

}
