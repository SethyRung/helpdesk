package com.sethy.service.comment.controller;

import com.sethy.service.common.api_response.ApiResponse;
import com.sethy.service.common.util.JwtUtil;
import com.sethy.service.comment.dto.CommentResponse;
import com.sethy.service.comment.dto.CreateCommentRequest;
import com.sethy.service.comment.dto.UpdateCommentRequest;
import com.sethy.service.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@Tag(name = "Comments", description = "Endpoints for managing ticket comments")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(
            description = "Retrieve all comments for a specific ticket. Only accessible by ticket owner or admins."
    )
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsForTicket(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long ticketId,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        List<CommentResponse> comments = commentService.getCommentsForTicket(ticketId, username);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping
    @Operation(
            description = "Add a new comment to a ticket. Only accessible by ticket owner or admins."
    )
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long ticketId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        CommentResponse comment = commentService.createComment(ticketId, request, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(comment, "Comment created successfully"));
    }

    @PutMapping("/{commentId}")
    @Operation(
            description = "Update an existing comment. Only accessible by comment author or admins."
    )
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long ticketId,
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        CommentResponse comment = commentService.updateComment(ticketId, commentId, request, username);
        return ResponseEntity.ok(ApiResponse.success(comment, "Comment updated successfully"));
    }

    @DeleteMapping("/{commentId}")
    @Operation(
            description = "Delete a comment from a ticket. Only accessible by comment author or admins."
    )
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long ticketId,
            @Parameter(description = "Comment ID", required = true)
            @PathVariable Long commentId,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        commentService.deleteComment(ticketId, commentId, username);
        return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
    }
}
