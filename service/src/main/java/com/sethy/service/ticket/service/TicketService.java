package com.sethy.service.ticket.service;

import com.sethy.service.ticket.dto.CreateTicketRequest;
import com.sethy.service.ticket.dto.TicketResponse;
import com.sethy.service.ticket.dto.UpdateStatusRequest;
import com.sethy.service.ticket.dto.UpdateTicketRequest;

import java.util.List;

public interface TicketService {

    TicketResponse createTicket(CreateTicketRequest request, String username, String email, String firstName, String lastName);

    TicketResponse getTicketById(Long id);

    List<TicketResponse> getAllTickets();

    List<TicketResponse> getMyTickets(String username);

    TicketResponse updateTicketAsUser(Long id, UpdateTicketRequest request, String username, boolean isAdmin);

    TicketResponse updateTicketAsAdmin(Long id, UpdateTicketRequest request);

    TicketResponse updateTicketStatus(Long id, UpdateStatusRequest request);

    void deleteTicket(Long id);
}
