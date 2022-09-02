package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.StatusOfItem;

import java.sql.Timestamp;
import java.util.List;

@Repository
//@EnableJpaRepositories
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartAfterAndEndBefore(Long bookerId, Timestamp start, Timestamp end, Sort sort);

    //Будущие задачи
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, Timestamp now, Sort sort);

    //Прошлые задачи
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Timestamp end, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, StatusOfItem status, Sort sort);

    List<Booking> findByItemId(Long itemId, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "order by b.start asc ")
    List<Booking> searchBookingByOwnerId(Long owner);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc ")
    List<Booking> searchBookingByOwnerIdCurrent(Long userId, Timestamp timestamp);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end < ?2 " +
            "order by b.start asc ")
    List<Booking> searchBookingByOwnerIdPast(Long userId, Timestamp timestamp);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc ")
    List<Booking> searchBookingByOwnerIdFuture(Long userId, Timestamp timestamp);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " +
            "order by b.start asc ")
    List<Booking> searchBookingByOwnerIdWaitingAndRejected(Long owner, StatusOfItem status);

//    SELECT * FROM book
//    @Query(" select i from Item i " +
//            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
//            " or upper(i.description) like upper(concat('%', ?1, '%'))")
//    List<Item> search(String text);

    List<Booking> findByItemIdAndBookerId(Long itemId, Long booker, Sort sort);
}
