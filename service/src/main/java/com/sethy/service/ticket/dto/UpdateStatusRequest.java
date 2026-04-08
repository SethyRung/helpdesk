package com.sethy.service.ticket.dto;

import com.sethy.service.ticket.model.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request object for updating ticket status (admin only)")
public class UpdateStatusRequest {

    @Schema(description = "New status for the ticket", example = "IN_PROGRESS")
    private TicketStatus status;
}
