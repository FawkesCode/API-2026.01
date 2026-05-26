package com.fawkes.api.Services;

import com.fawkes.api.DTOs.*;
import com.fawkes.api.DTOs.Request.AddProposalRequest;
import com.fawkes.api.DTOs.Request.ProposalItemRequest;
import com.fawkes.api.Entities.*;
import com.fawkes.api.Repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationProposalRepository proposalRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductsRepository productsRepository;

    public List<QuotationResponse> listAll() {
        return quotationRepository.findAll().stream()
                .map(QuotationResponse::fromEntity)
                .toList();
    }

    public QuotationResponse getById(Long id) {
        Quotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));
        return QuotationResponse.fromEntity(quotation);
    }

    @Transactional
    public QuotationResponse createQuotation(Long purchaseOrderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));

        if (order.getStatus() != PurchaseOrder.Status.confirmed) {
            throw new IllegalArgumentException(
                    "Only confirmed orders can enter quotation. Current status: " + order.getStatus());
        }

        if (quotationRepository.existsByPurchaseOrderId(purchaseOrderId)) {
            throw new IllegalArgumentException("A quotation already exists for this purchase order");
        }

        Quotation quotation = new Quotation();
        quotation.setPurchaseOrder(order);
        quotation.setQuotationNumber(generateQuotationNumber());
        quotation.setStatus(Quotation.Status.in_progress);
        quotation.setProposals(new ArrayList<>());

        quotation = quotationRepository.save(quotation);

        return QuotationResponse.fromEntity(quotation);
    }

    @Transactional
    public QuotationProposalResponse addProposal(Long quotationId, AddProposalRequest request) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        if (quotation.getStatus() != Quotation.Status.in_progress) {
            throw new IllegalArgumentException("Cannot add proposals to quotation with status: " + quotation.getStatus());
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Proposal must have at least one item");
        }

        if (request.getDeliveryDays() == null || request.getDeliveryDays() <= 0) {
            throw new IllegalArgumentException("Delivery days must be greater than zero");
        }

        Suppliers supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found"));

        QuotationProposal proposal = new QuotationProposal();
        proposal.setQuotation(quotation);
        proposal.setSupplier(supplier);
        proposal.setDeliveryDays(request.getDeliveryDays());
        proposal.setPaymentConditions(request.getPaymentConditions());
        proposal.setStatus(QuotationProposal.Status.under_review);

        List<QuotationProposalItem> items = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (ProposalItemRequest itemReq : request.getItems()) {
            if (itemReq.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Os valores devem ser maiores que zero");
            }
            if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }

            Products product = productsRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemReq.getProductId()));

            QuotationProposalItem item = new QuotationProposalItem();
            item.setProposal(proposal);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setTotalPrice(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));

            totalValue = totalValue.add(item.getTotalPrice());
            items.add(item);
        }

        proposal.setItems(items);
        proposal.setTotalValue(totalValue);

        proposal = proposalRepository.save(proposal);

        return QuotationProposalResponse.fromEntity(proposal);
    }

    public QuotationComparisonResponse compareProposals(Long quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        List<QuotationProposal> proposals = proposalRepository.findByQuotationId(quotationId);

        List<QuotationProposalResponse> proposalResponses = proposals.stream()
                .map(QuotationProposalResponse::fromEntity)
                .toList();

        return QuotationComparisonResponse.fromProposals(
                quotationId,
                quotation.getQuotationNumber(),
                proposalResponses
        );
    }

    @Transactional
    public QuotationResponse selectWinner(Long quotationId, Long proposalId, String justification) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        if (quotation.getStatus() != Quotation.Status.in_progress) {
            throw new IllegalArgumentException("Cannot select winner for quotation with status: " + quotation.getStatus());
        }

        QuotationProposal winner = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("Proposal not found"));

        if (!winner.getQuotation().getId().equals(quotationId)) {
            throw new IllegalArgumentException("Proposal does not belong to this quotation");
        }

        if (justification == null || justification.isBlank()) {
            throw new IllegalArgumentException("Justification is required when selecting a winner");
        }

        List<QuotationProposal> allProposals = proposalRepository.findByQuotationId(quotationId);
        for (QuotationProposal p : allProposals) {
            if (p.getId().equals(proposalId)) {
                p.setStatus(QuotationProposal.Status.selected);
                p.setJustification(justification);
            } else {
                p.setStatus(QuotationProposal.Status.not_selected);
            }
            proposalRepository.save(p);
        }

        quotation.setStatus(Quotation.Status.completed);
        quotation = quotationRepository.save(quotation);

        return QuotationResponse.fromEntity(quotation);
    }

    @Transactional
    public QuotationResponse cancelQuotation(Long quotationId, String justification) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new IllegalArgumentException("Quotation not found"));

        if (quotation.getStatus() != Quotation.Status.in_progress) {
            throw new IllegalArgumentException("Cannot cancel quotation with status: " + quotation.getStatus());
        }

        if (justification == null || justification.isBlank()) {
            throw new IllegalArgumentException("Justification is required when cancelling a quotation");
        }

        List<QuotationProposal> allProposals = proposalRepository.findByQuotationId(quotationId);
        for (QuotationProposal p : allProposals) {
            p.setStatus(QuotationProposal.Status.not_selected);
            proposalRepository.save(p);
        }

        quotation.setStatus(Quotation.Status.cancelled);
        quotation = quotationRepository.save(quotation);

        return QuotationResponse.fromEntity(quotation);
    }

    private String generateQuotationNumber() {
        long count = quotationRepository.count();
        int year = Year.now().getValue();
        return String.format("COT-%d-%04d", year, count + 1);
    }
}
