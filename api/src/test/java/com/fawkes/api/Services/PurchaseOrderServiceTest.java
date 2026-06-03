package com.fawkes.api.Services;

import com.fawkes.api.DTOs.Request.ConfirmOrderRequest;
import com.fawkes.api.Entities.Products;
import com.fawkes.api.Entities.PurchaseOrder;
import com.fawkes.api.Exceptions.RegraDeNegocioException;
import com.fawkes.api.Repositories.OrderNoteRepository;
import com.fawkes.api.Repositories.ProductsRepository;
import com.fawkes.api.Repositories.PurchaseOrderEventRepository;
import com.fawkes.api.Repositories.PurchaseOrderRepository;
import com.fawkes.api.Repositories.SupplierRepository;
import com.fawkes.api.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock PurchaseOrderRepository purchaseOrderRepository;
    @Mock PurchaseOrderEventRepository purchaseOrderEventRepository;
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
        assertThat(result.getPurchaseJustification()).isEqualTo("Comprado no fornecedor X");
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
    void cancelOrder_persisteReasonEmPurchaseJustification() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.cancelOrder(1L, "Fora do orçamento");

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.cancelled);
        assertThat(result.getPurchaseJustification()).isEqualTo("Fora do orçamento");
    }

    @Test
    void markAsProblem_gravaProblemJustification_ePreservaCompraENotes() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.received);
        order.setNotes("Observação do solicitante");
        order.setPurchaseJustification("Comprado no fornecedor X");
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.markAsProblem(1L, "Item veio quebrado");

        assertThat(result.getProblemJustification()).isEqualTo("Item veio quebrado");
        assertThat(result.getPurchaseJustification()).isEqualTo("Comprado no fornecedor X");
        assertThat(result.getNotes()).isEqualTo("Observação do solicitante");
    }

    @Test
    void fillItemPrices_dePendingVaiParaQuoted_eGravaEvento() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.pending);
        var item = new com.fawkes.api.Entities.PurchaseOrderItem();
        item.setId(7L);
        item.setQuantity(2);
        order.setItems(new java.util.ArrayList<>(java.util.List.of(item)));
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(
                java.util.List.of(new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest
                        .ItemPriceEntry(7L, new BigDecimal("10.00"))));

        PurchaseOrder result = service.fillItemPrices(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        assertThat(result.getItems().get(0).getUnitPrice()).isEqualByComparingTo("10.00");
        assertThat(result.getTotalValue()).isEqualByComparingTo("20.00");
        verify(purchaseOrderEventRepository).save(any());
    }

    @Test
    void fillItemPrices_emQuoted_mantemQuoted_eNaoGravaEvento() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.quoted);
        var item = new com.fawkes.api.Entities.PurchaseOrderItem();
        item.setId(7L);
        item.setQuantity(2);
        order.setItems(new java.util.ArrayList<>(java.util.List.of(item)));
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(
                java.util.List.of(new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest
                        .ItemPriceEntry(7L, new BigDecimal("12.50"))));

        PurchaseOrder result = service.fillItemPrices(1L, req);

        assertThat(result.getStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        assertThat(result.getItems().get(0).getUnitPrice()).isEqualByComparingTo("12.50");
        verifyNoInteractions(purchaseOrderEventRepository);
    }

    @Test
    void fillItemPrices_rejeitaQuandoNaoEhPendingNemQuoted() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.confirmed);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        var req = new com.fawkes.api.DTOs.Request.UpdateItemPricesRequest(java.util.List.of());

        assertThatThrownBy(() -> service.fillItemPrices(1L, req))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void listAllEvents_mapeiaEventosParaDTO_maisRecentesPrimeiro() {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(5L);
        order.setItems(new java.util.ArrayList<>());
        var event = new com.fawkes.api.Entities.PurchaseOrderEvent();
        event.setId(99L);
        event.setPurchaseOrder(order);
        event.setFromStatus(PurchaseOrder.Status.pending);
        event.setToStatus(PurchaseOrder.Status.quoted);
        event.setPerformedBy("gerente@x");
        event.setOccurredAt(LocalDateTime.of(2026, 6, 2, 10, 0));
        when(purchaseOrderEventRepository.findAllByOrderByOccurredAtDesc())
                .thenReturn(java.util.List.of(event));

        var result = service.listAllEvents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).orderId()).isEqualTo(5L);
        assertThat(result.get(0).toStatus()).isEqualTo(PurchaseOrder.Status.quoted);
        assertThat(result.get(0).performedBy()).isEqualTo("gerente@x");
    }

    @Test
    void listAllEvents_montaLabelComProdutoEContagem() {
        PurchaseOrder order = new PurchaseOrder();
        order.setId(5L);
        var p1 = new com.fawkes.api.Entities.Products(); p1.setProductName("Caneta");
        var i1 = new com.fawkes.api.Entities.PurchaseOrderItem(); i1.setProduct(p1);
        var i2 = new com.fawkes.api.Entities.PurchaseOrderItem();
        order.setItems(new java.util.ArrayList<>(java.util.List.of(i1, i2)));
        var event = new com.fawkes.api.Entities.PurchaseOrderEvent();
        event.setId(1L); event.setPurchaseOrder(order);
        event.setToStatus(PurchaseOrder.Status.confirmed);
        event.setOccurredAt(java.time.LocalDateTime.now());
        when(purchaseOrderEventRepository.findAllByOrderByOccurredAtDesc())
                .thenReturn(java.util.List.of(event));

        var result = service.listAllEvents();

        assertThat(result.get(0).orderLabel()).isEqualTo("Pedido #5 — Caneta (+1)");
    }

    @Test
    void addItem_aceitaPrecoZero() {
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrder.Status.draft);
        order.setItems(new ArrayList<>());
        Products product = mock(Products.class);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productsRepository.findById(10L)).thenReturn(Optional.of(product));
        when(purchaseOrderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder result = service.addItem(1L, 10L, 5, BigDecimal.ZERO);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getUnitPrice()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
