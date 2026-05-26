package com.fawkes.api.Repositories;

import com.fawkes.api.Entities.QuotationProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationProposalRepository extends JpaRepository<QuotationProposal, Long> {
    List<QuotationProposal> findByQuotationId(Long quotationId);
    List<QuotationProposal> findByQuotationIdAndStatus(Long quotationId, QuotationProposal.Status status);
    long countByQuotationId(Long quotationId);
}
