package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Data
@Builder
public class ItemDtoWithComments {

    private Long id;
//    private String name;
//    private String description;
    private Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private
//    private Long request;
    private List<Comment> comment;

}
