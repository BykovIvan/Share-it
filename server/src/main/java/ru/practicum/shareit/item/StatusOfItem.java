package ru.practicum.shareit.item;

public enum StatusOfItem {
    WAITING,                //новое бронирование, ожидает бронирования
    APPROVED,               //бронирование подтверждено владельцем
    REJECTED,               //бронирование откланено владельцем
    CANCELED                //бронирование отменено создателем
}
