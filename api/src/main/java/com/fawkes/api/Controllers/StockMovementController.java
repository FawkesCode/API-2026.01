package com.fawkes.api.Controllers;

import com.fawkes.api.DTOs.ActivityDTO;
import com.fawkes.api.Entities.ProductInputs;
import com.fawkes.api.Entities.ProductOutputs;
import com.fawkes.api.Entities.Ticket;
import com.fawkes.api.Exceptions.RecursoNaoEncontradoException;
import com.fawkes.api.Repositories.TicketRepository;
import com.fawkes.api.Services.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/stock/movements")
@RequiredArgsConstructor
public class StockMovementController {

    private final StockMovementService stockMovementService;
    private final TicketRepository ticketRepository;

    // ----------------------------------------------------------------
    // GETs de histórico — necessários para a tela de Atividade Recente
    // ----------------------------------------------------------------

    @GetMapping("/inputs")
    public ResponseEntity<List<ProductInputs>> listInputs() {
        return ResponseEntity.ok(stockMovementService.listAllInputs());
    }

    @GetMapping("/outputs")
    public ResponseEntity<List<ProductOutputs>> listOutputs() {
        return ResponseEntity.ok(stockMovementService.listAllOutputs());
    }

    // ----------------------------------------------------------------
    // POSTs existentes — mantidos sem alteração
    // ----------------------------------------------------------------

    @PostMapping("/input")
    public ResponseEntity<ProductInputs> registerInput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        ProductInputs input = stockMovementService.registerInput(stockId, productId, quantity);
        return ResponseEntity.ok(input);
    }

    @PostMapping("/output")
    public ResponseEntity<ProductOutputs> registerOutput(
            @RequestParam Long stockId,
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long orderId) {
        Ticket ticket = null;
        if (orderId != null) {
            ticket = ticketRepository.findById(orderId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Ticket não encontrado: " + orderId));
        }
        ProductOutputs output = stockMovementService.registerOutput(stockId, productId, quantity, ticket);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityDTO>> listActivity() {
        return ResponseEntity.ok(stockMovementService.listActivity());
    }


}