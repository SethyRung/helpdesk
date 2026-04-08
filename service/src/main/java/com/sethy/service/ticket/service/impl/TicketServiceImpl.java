package com.sethy.service.ticket.service.impl;

import com.sethy.service.ticket.dto.CreateTicketRequest;
import com.sethy.service.ticket.dto.TicketResponse;
import com.sethy.service.ticket.dto.UpdateStatusRequest;
import com.sethy.service.ticket.dto.UpdateTicketRequest;
import com.sethy.service.ticket.entity.Ticket;
import com.sethy.service.ticket.repository.TicketRepository;
import com.sethy.service.ticket.service.TicketService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public TicketResponse createTicket(CreateTicketRequest request, String username, String email, String firstName, String lastName) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCreatedBy(username);
        ticket.setCreatedByEmail(email != null ? email : "");
        ticket.setCreatedByFirstName(firstName != null ? firstName : "");
        ticket.setCreatedByLastName(lastName != null ? lastName : "");

        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(savedTicket);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = findTicketByIdOrThrow(id);
        return TicketResponse.fromEntity(ticket);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<TicketResponse> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(TicketResponse::fromEntity)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<TicketResponse> getMyTickets(String username) {
        List<Ticket> tickets = ticketRepository.findByCreatedBy(username);
        return tickets.stream()
                .map(TicketResponse::fromEntity)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public TicketResponse updateTicketAsUser(Long id, UpdateTicketRequest request, String username, boolean isAdmin) {
        Ticket ticket = findTicketByIdOrThrow(id);

        // Verify ownership (admin can bypass this check)
        if (!isAdmin && !ticket.getCreatedBy().equals(username)) {
            throw new IllegalArgumentException("You can only update your own tickets");
        }

        // Partial update - only update non-null fields
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(savedTicket);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TicketResponse updateTicketAsAdmin(Long id, UpdateTicketRequest request) {
        Ticket ticket = findTicketByIdOrThrow(id);

        // Partial update - only update non-null fields
        if (request.getTitle() != null) {
            ticket.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            ticket.setDescription(request.getDescription());
        }
        if (request.getPriority() != null) {
            ticket.setPriority(request.getPriority());
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(savedTicket);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TicketResponse updateTicketStatus(Long id, UpdateStatusRequest request) {
        Ticket ticket = findTicketByIdOrThrow(id);
        ticket.setStatus(request.getStatus());

        Ticket savedTicket = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(savedTicket);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTicket(Long id) {
        Ticket ticket = findTicketByIdOrThrow(id);
        ticketRepository.delete(ticket);
    }

    private Ticket findTicketByIdOrThrow(Long id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        return ticket.orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));
    }
}
