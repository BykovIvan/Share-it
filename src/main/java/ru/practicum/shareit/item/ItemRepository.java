package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    List<Item> findByOwnerId(Long userID);

//    @Query(" select i from Item i " +
//            "where i.id = ?1 " +
//            "and i.owner.id  = ?2")

//    @Query(" select b from Booking b " +
//            "JOIN Item i on b.item.id = i.id "+
//            "where i.owner.id = ?1 " +
//            "order by b.start asc ")

//        @Query(" select i from Item i " +
//                "JOIN User u on i.owner.id = u.id "+
//                "where i.id = ?1 " +
//                "and u.id  = ?2")
    Item findByIdAndAvailable(Long itemId, Boolean available);

}
