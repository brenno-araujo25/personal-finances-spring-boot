package io.github.brenno_araujo25.personal_finances.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.brenno_araujo25.personal_finances.dto.TransactionRequest;
import io.github.brenno_araujo25.personal_finances.dto.TransactionResponse;
import io.github.brenno_araujo25.personal_finances.service.TransactionService;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
        @RequestBody
        TransactionRequest transactionRequest
    ) {
        TransactionResponse transactionResponse = transactionService.createTransaction(transactionRequest);
        return ResponseEntity.ok(transactionResponse);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getUserTransactions() {
        List<TransactionResponse> transactions = transactionService.getUserTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
        @PathVariable
        UUID transactionId
    ) {
        TransactionResponse transaction = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(
        @PathVariable
        UUID transactionId,
        @RequestBody
        TransactionRequest transactionRequest
    ) {
        TransactionResponse transaction = transactionService.updateTransaction(transactionId, transactionRequest);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
        @PathVariable
        UUID transactionId
    ) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

}
