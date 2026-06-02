package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Repositories.OrderNoteRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import com.fawkes.api.Repositories.PurchaseOrderRepository;
import com.fawkes.api.Repositories.SupplierRepository;
import com.fawkes.api.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock PurchaseOrderRepository purchaseOrderRepository;
    @Mock SupplierRepository supplierRepository;
    @Mock UserRepository userRepository;
    @Mock ProductsRepository productsRepository;
    @Mock StockMovementService stockMovementService;
    @Mock OrderNoteRepository orderNoteRepository;

    @InjectMocks PurchaseOrderService service;

    @Test
    void confirmOrder_persisteDataEntregaEJustificativa_eMarcaConfirmed() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        LocalDateTime date = LocalDateTime.of(2026, 6, 4, 23, 59, 59);
        ConfirmOrderRequest req = new ConfirmOrderRequest(date, "Comprado no fornecedor X");

        PurchaseOrder result = service.confirmOrder(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.confirmed);
        assertThat(result.getExpectedDeliveryDate()).isEqualTo(date);
        assertThat(result.getDecisionReason()).isEqualTo("Comprado no fornecedor X");
    }

    @Test
    void confirmOrder_rejeitaQuandoNaoEstaQuoted() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.pending);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.confirmOrder(1L, new ConfirmOrderRequest(null, null)))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void cancelOrder_persisteReasonEmDecisionReason() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.cancelOrder(1L, "Fora do orçamento");

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.cancelled);
        assertThat(result.getDecisionReason()).isEqualTo("Fora do orçamento");
    }

    @Test
    void markAsProblem_gravaDecisionReason_ePreservaNotes() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.received);
        order.setNotes("Observação do solicitante");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.markAsProblem(1L, "Item veio quebrado");

        assertThat(result.getDecisionReason()).isEqualTo("Item veio quebrado");
        assertThat(result.getNotes()).isEqualTo("Observação do solicitante");
    }
}
