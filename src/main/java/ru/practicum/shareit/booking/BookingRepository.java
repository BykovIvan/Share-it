package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.StatusOfItem;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartAfterAndEndBefore(Long bookerId, Timestamp start, Timestamp end, Sort sort);

    //Будущие задачи
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, Timestamp now, Sort sort);

    //Прошлые задачи
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Timestamp end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, StatusOfItem status, Sort sort);

    List<Booking> findByItemId(Long itemId);

//    List<Booking> findByOwnerId(Long owner, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i "+
            "where upper(i.owner) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> searchBookingByOwnerId(Long owner);

//    @Query(" select i from Item i " +
//            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
//            " or upper(i.description) like upper(concat('%', ?1, '%'))")
//    List<Item> search(String text);
}
