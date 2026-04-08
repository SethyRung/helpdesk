package com.sethy.service.ticket.controller;

import com.sethy.service.common.api_response.ApiResponse;
import com.sethy.service.common.util.JwtUtil;
import com.sethy.service.ticket.dto.CreateTicketRequest;
import com.sethy.service.ticket.dto.TicketResponse;
import com.sethy.service.ticket.dto.UpdateStatusRequest;
import com.sethy.service.ticket.dto.UpdateTicketRequest;
import com.sethy.service.ticket.service.TicketService;
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
@RequestMapping("/api/tickets")
@Tag(name = "Tickets")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping
    @Operation(
            description = "Retrieve all tickets in the system. ADMIN only."
    )
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets() {
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/my")
    @Operation(
            description = "Retrieve tickets created by the authenticated user."
    )
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        List<TicketResponse> tickets = ticketService.getMyTickets(username);
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Retrieve a specific ticket by its ID."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long id) {
        TicketResponse ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    @PostMapping
    @Operation(
            description = "Create a new ticket. The createdBy field is automatically set to the authenticated user."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        String email = JwtUtil.extractEmail(authentication);
        String firstName = JwtUtil.extractFirstName(authentication);
        String lastName = JwtUtil.extractLastName(authentication);
        TicketResponse ticket = ticketService.createTicket(request, username, email, firstName, lastName);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ticket, "Ticket created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(
            description = "Update any ticket with all fields including status. ADMIN only."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketAsAdmin(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateTicketRequest request) {
        TicketResponse ticket = ticketService.updateTicketAsAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket updated successfully"));
    }

    @PatchMapping("/{id}")
    @Operation(
            description = "Update own ticket with title, description, priority. Cannot update status. Owner only (admin can update any ticket)."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketAsUser(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long id,
            @RequestBody UpdateTicketRequest request,
            Authentication authentication) {
        String username = JwtUtil.extractUsername(authentication);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        TicketResponse ticket = ticketService.updateTicketAsUser(id, request, username, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            description = "Update ticket status. ADMIN only."
    )
    public ResponseEntity<ApiResponse<TicketResponse>> updateTicketStatus(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        TicketResponse ticket = ticketService.updateTicketStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Ticket status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a ticket by ID. ADMIN only."
    )
    public ResponseEntity<ApiResponse<Void>> deleteTicket(
            @Parameter(description = "Ticket ID", required = true)
            @PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ticket deleted successfully"));
    }
}
