package com.sethy.service.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Response object representing a comment")
public class CommentResponse {

    @Schema(description = "Unique identifier of the comment", example = "1")
    private Long id;

    @Schema(description = "Content of the comment", example = "This issue has been resolved")
    private String content;

    @Schema(description = "Username of the user who created the comment", example = "john.doe")
    private String author;

    @Schema(description = "Timestamp when the comment was created", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the comment was last updated", example = "2025-01-15T11:45:00")
    private LocalDateTime updatedAt;

    public static CommentResponse fromEntity(com.sethy.service.comment.entity.Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setAuthor(comment.getAuthor());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}
