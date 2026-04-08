package com.sethy.service.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new comment")
public class CreateCommentRequest {

    @Schema(description = "Content of the comment", example = "This issue has been resolved")
    @NotBlank(message = "Content cannot be blank")
    @Size(max = 5000, message = "Content cannot exceed 5000 characters")
    private String content;
}
