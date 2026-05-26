package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.*;
import com.fawkes.api.DTOs.Request.AddProposalRequest;
import com.fawkes.api.DTOs.Request.CancelQuotationRequest;
import com.fawkes.api.DTOs.Request.CreateQuotationRequest;
import com.fawkes.api.DTOs.Request.SelectWinnerRequest;
import com.fawkes.api.Services.QuotationPdfService;
import com.fawkes.api.Services.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {

    private final QuotationService quotationService;
    private final QuotationPdfService quotationPdfService;

    @GetMapping
    public ResponseEntity<List<QuotationResponse>> listAll() {
        return ResponseEntity.ok(quotationService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.getById(id));
    }

    @PostMapping
    public ResponseEntity<QuotationResponse> create(@RequestBody CreateQuotationRequest request) {
        return ResponseEntity.ok(quotationService.createQuotation(request.getPurchaseOrderId()));
    }

    @PostMapping("/{id}/proposals")
    public ResponseEntity<QuotationProposalResponse> addProposal(
            @PathVariable Long id,
            @RequestBody AddProposalRequest request) {
        return ResponseEntity.ok(quotationService.addProposal(id, request));
    }

    @GetMapping("/{id}/proposals/compare")
    public ResponseEntity<QuotationComparisonResponse> compareProposals(@PathVariable Long id) {
        return ResponseEntity.ok(quotationService.compareProposals(id));
    }

    @PostMapping("/{id}/proposals/{proposalId}/select")
    public ResponseEntity<QuotationResponse> selectWinner(
            @PathVariable Long id,
            @PathVariable Long proposalId,
            @RequestBody SelectWinnerRequest request) {
        return ResponseEntity.ok(quotationService.selectWinner(id, proposalId, request.getJustification()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<QuotationResponse> cancel(
            @PathVariable Long id,
            @RequestBody CancelQuotationRequest request) {
        return ResponseEntity.ok(quotationService.cancelQuotation(id, request.getJustification()));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        Resource pdf = quotationPdfService.generatePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cotacao-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
