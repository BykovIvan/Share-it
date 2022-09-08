package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.StatusOfItem;


import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerId(Long bookerId, Sort sort);
    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
//           + "order by b.id desc ")
    List<Booking> findByBookerIdByUserId(Long bookerId, Timestamp timestamp, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
    Page<Booking> findByBookerIdByUserId(Long bookerId, Timestamp timestamp, Pageable pageable);

    //Будущие задачи
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, Timestamp now, Sort sort);
    List<Booking> findByBookerIdAndStartAfter(Long bookerId, Timestamp now, Pageable pageable);

    //Прошлые задачи
    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Timestamp end, Sort sort);
    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, Timestamp end, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, StatusOfItem status, Sort sort);
    Page<Booking> findByBookerIdAndStatus(Long bookerId, StatusOfItem status, Pageable pageable);

    List<Booking> findByItemId(Long itemId, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 ")
//            + "order by b.id desc ")
    List<Booking> searchOwnerByOwnerId(Long owner, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 ")
//            + "order by b.id desc ")
    Page<Booking> searchOwnerByOwnerId(Long owner, Pageable pageable);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
//            + "order by b.id desc ")
    List<Booking> searchOwnerByOwnerIdCurrent(Long userId, Timestamp timestamp, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 ")
//            + "order by b.id desc ")
    Page<Booking> searchOwnerByOwnerIdCurrent(Long userId, Timestamp timestamp, Pageable pageable);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end < ?2 " )
//            + "order by b.id desc ")
    List<Booking> searchOwnerByOwnerIdPast(Long userId, Timestamp timestamp, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end < ?2 " )
//            + "order by b.id desc ")
    Page<Booking> searchOwnerByOwnerIdPast(Long userId, Timestamp timestamp, Pageable pageable);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "and b.end > ?2 " )
//            + "order by b.id desc ")
    List<Booking> searchOwnerByOwnerIdFuture(Long userId, Timestamp timestamp, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.start > ?2 " +
            "and b.end > ?2 " )
//            + "order by b.id desc ")
    Page<Booking> searchOwnerByOwnerIdFuture(Long userId, Timestamp timestamp, Pageable pageable);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " )
//            + "order by b.id desc ")
    List<Booking> searchOwnerByOwnerIdWaitingAndRejected(Long owner, StatusOfItem status, Sort sort);

    @Query(" select b from Booking b " +
            "JOIN Item i on b.item.id = i.id "+
            "where i.owner.id = ?1 " +
            "and b.status = ?2 " )
//            + "order by b.id desc ")
    Page<Booking> searchOwnerByOwnerIdWaitingAndRejected(Long owner, StatusOfItem status, Pageable pageable);

    List<Booking> findByItemIdAndBookerId(Long itemId, Long booker, Sort sort);
}
