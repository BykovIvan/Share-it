package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> search(String text);

    Item findByIdAndOwnerId(Long itemId, Long userID);

    List<Item> findByOwnerId(Long userID, Sort sort);

    Item findByIdAndAvailable(Long itemId, Boolean available);

    Item findByRequestId(Long requestId);

}
