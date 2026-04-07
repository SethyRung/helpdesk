package com.sethy.service.ticket.dto;

import com.sethy.service.ticket.entity.Ticket;
import com.sethy.service.ticket.model.TicketPriority;
import com.sethy.service.ticket.model.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response object representing a ticket")
public class TicketResponse {

    @Schema(description = "Unique identifier of the ticket", example = "1")
    private Long id;

    @Schema(description = "Title of the ticket", example = "Login issue")
    private String title;

    @Schema(description = "Detailed description of the ticket", example = "Unable to login to the dashboard")
    private String description;

    @Schema(description = "Priority level of the ticket", example = "MEDIUM")
    private TicketPriority priority;

    @Schema(description = "Current status of the ticket", example = "OPEN")
    private TicketStatus status;

    @Schema(description = "Information about the user who created the ticket")
    private UserSummary createdBy;

    @Schema(description = "Timestamp when the ticket was created", example = "2025-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the ticket was last updated", example = "2025-01-15T11:45:00")
    private LocalDateTime updatedAt;

    public static TicketResponse fromEntity(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());

        UserSummary creator = new UserSummary();
        creator.setUsername(ticket.getCreatedBy());
        creator.setEmail(ticket.getCreatedByEmail());
        creator.setFirstName(ticket.getCreatedByFirstName());
        creator.setLastName(ticket.getCreatedByLastName());
        response.setCreatedBy(creator);

        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        this.priority = priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public UserSummary getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummary createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
