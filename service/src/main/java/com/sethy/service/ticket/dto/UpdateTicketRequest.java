package com.sethy.service.ticket.dto;

import com.sethy.service.ticket.model.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for updating a ticket (users can update title, description, priority)")
public class UpdateTicketRequest {

    @Schema(description = "Title of the ticket", example = "Login issue - updated")
    private String title;

    @Schema(description = "Detailed description of the ticket", example = "Updated description")
    private String description;

    @Schema(description = "Priority level of the ticket", example = "HIGH")
    private TicketPriority priority;
}
