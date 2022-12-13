package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated());
    }
}
