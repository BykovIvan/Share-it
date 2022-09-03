package ru.practicum.shareit.item;

import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    //    @Mapping(target = "id", ignore = true)
//    Item map(ItemDto itemDto);

//    ItemDto map(Item item);

//        @InheritConfiguration
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);

}
