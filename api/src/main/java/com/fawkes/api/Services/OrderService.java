package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.OrderRequest;
import com.fawkes.api.DTOs.Request.OrderStatusRequest;
import com.fawkes.api.DTOs.Response.OrderResponse;
import com.fawkes.api.Entities.*;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SupplierRepository supplierRepository;
    private final OrderNoteRepository orderNoteRepository;

    public List<OrderResponse> listAll() {
        return ticketRepository.findAllOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> listByUser(Long userId) {
        return ticketRepository.findByUsuarioId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> listByDepartment(Long departmentId) {
        return ticketRepository.findByDepartamentoId(departmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> listByStatus(TicketEnum status) {
        return ticketRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getById(Long id) {
        return toResponse(findTicketOrThrow(id));
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        Users user = userRepository.findByIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado ou inativo."));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Departamento não encontrado."));

        Suppliers supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fornecedor não encontrado."));

        if (request.getValue() == null || request.getValue().signum() <= 0)
            throw new RegraDeNegocioException("O valor do pedido deve ser maior que zero.");

        if (request.getQuantidadeItens() == null || request.getQuantidadeItens() < 1)
            throw new RegraDeNegocioException("A quantidade de itens deve ser pelo menos 1.");

        Ticket ticket = new Ticket();
        ticket.setUsuario(user);
        ticket.setDepartamento(department);
        ticket.setFornecedor(supplier);
        ticket.setFormaPagamento(request.getFormaPagamento());
        ticket.setDescricao(request.getDescricao());
        ticket.setQuantidadeItens(request.getQuantidadeItens());
        ticket.setValor(request.getValue());
        ticket.setStatus(TicketEnum.pendente);

        if (request.getOrderNoteId() != null) {
            OrderNote note = orderNoteRepository.findById(request.getOrderNoteId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Nota fiscal não encontrada."));
            ticket.setNotaFiscal(note);
        }

        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatusRequest request) {
        Ticket ticket = findTicketOrThrow(id);
        validateStatusTransition(ticket.getStatus(), request.getStatus());
        ticket.setStatus(request.getStatus());
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public OrderResponse processOrder(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        validateStatusTransition(ticket.getStatus(), TicketEnum.processando);
        ticket.setStatus(TicketEnum.processando);
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public OrderResponse concludeOrder(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        validateStatusTransition(ticket.getStatus(), TicketEnum.concluido);
        ticket.setStatus(TicketEnum.concluido);
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        if (ticket.getStatus() == TicketEnum.concluido)
            throw new RegraDeNegocioException("Não é possível cancelar um pedido já concluído.");
        ticket.setStatus(TicketEnum.cancelado);
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public OrderResponse attachOrderNote(Long id, Long orderNoteId) {
        Ticket ticket = findTicketOrThrow(id);
        if (ticket.getStatus() == TicketEnum.cancelado)
            throw new RegraDeNegocioException("Não é possível vincular nota fiscal a um pedido cancelado.");
        OrderNote note = orderNoteRepository.findById(orderNoteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nota fiscal não encontrada."));
        ticket.setNotaFiscal(note);
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional
    public void deleteOrder(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        if (ticket.getStatus() != TicketEnum.pendente)
            throw new RegraDeNegocioException("Somente pedidos com status 'pendente' podem ser excluídos.");
        ticketRepository.deleteById(id);
    }

    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado com id: " + id));
    }

    private void validateStatusTransition(TicketEnum current, TicketEnum next) {
        boolean valid = switch (current) {
            case pendente    -> next == TicketEnum.processando || next == TicketEnum.cancelado;
            case processando -> next == TicketEnum.concluido   || next == TicketEnum.cancelado;
            case concluido, cancelado -> false;
        };
        if (!valid)
            throw new RegraDeNegocioException("Transição de status inválida: " + current + " → " + next);
    }

    private OrderResponse toResponse(Ticket ticket) {
        OrderResponse r = new OrderResponse();
        r.setId(ticket.getId());
        r.setCreatedAt(ticket.getCreatedAt());
        r.setUpdatedAt(ticket.getUpdatedAt());
        r.setOrderDate(ticket.getDataPedido());
        r.setStatus(ticket.getStatus());
        r.setValue(ticket.getValor());
        r.setDescricao(ticket.getDescricao());
        r.setQuantidadeItens(ticket.getQuantidadeItens());
        r.setFormaPagamento(ticket.getFormaPagamento());

        if (ticket.getUsuario() != null) {
            r.setUserId(ticket.getUsuario().getId());
            r.setUserName(ticket.getUsuario().getUserName());
        }
        if (ticket.getDepartamento() != null) {
            r.setDepartmentId(ticket.getDepartamento().getId());
            r.setDepartmentName(ticket.getDepartamento().getDepartamentName());
        }
        if (ticket.getFornecedor() != null) {
            r.setSupplierId(ticket.getFornecedor().getId());
            r.setSupplierName(ticket.getFornecedor().getSupplierName());
        }
        if (ticket.getNotaFiscal() != null) {
            r.setOrderNoteId(ticket.getNotaFiscal().getId());
            r.setOrderNoteNumber(ticket.getNotaFiscal().getNumberNote());
        }
        return r;
    }
}