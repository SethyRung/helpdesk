package com.sethy.service.ticket.dto;

import com.sethy.service.ticket.model.TicketPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new ticket")
public class CreateTicketRequest {

    @Schema(description = "Title of the ticket", example = "Login issue")
    private String title;

    @Schema(description = "Detailed description of the ticket", example = "Unable to login to the dashboard")
    private String description;

    @Schema(description = "Priority level of the ticket", example = "MEDIUM")
    private TicketPriority priority;
}
