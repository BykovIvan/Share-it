package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    //TODO Найти комментарий по id вещи!
//    @Query(" select c from Comment c " +
//            "JOIN Item i on c.item.id = i.id "+
//            "where i.id = ?1 ")
    List<Comment> findAllByItemId(Long itemId);
}
