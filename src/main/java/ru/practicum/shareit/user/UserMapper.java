package ru.practicum.shareit.user;

import org.mapstruct.*;

/**
 * Класс для полного или частичного обновления через DTO для user
 */

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, componentModel = "spring")
public interface UserMapper {

//    @Mapping(target = "id", ignore = true)
    User map(UserDto userDto);

    UserDto map(User user);
//    @InheritConfiguration
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}

