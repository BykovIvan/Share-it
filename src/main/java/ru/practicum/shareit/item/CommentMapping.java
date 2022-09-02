package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.sql.Timestamp;

public class CommentMapping {
    public static CommentDto toCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().toLocalDateTime())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author){
        return Comment.builder()
                .text(commentDto.getText())
                .created(Timestamp.valueOf(commentDto.getCreated()))
                .item(item)
                .author(author)
                .build();
    }

}
