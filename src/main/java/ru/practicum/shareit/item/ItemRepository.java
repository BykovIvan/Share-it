package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    List<Item> searchWithPageable(String text, Pageable pageable);
    Item findByIdAndOwnerId(Long itemId, Long userID);
    List<Item> findByOwnerId(Long userID, Sort sort);
    Page<Item> findByOwnerId(Long userID, Pageable pageable);
    Item findByIdAndAvailable(Long itemId, Boolean available);
    List<Item> findByRequestId(Long requestId);

}
