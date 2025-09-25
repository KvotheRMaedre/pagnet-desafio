package tech.kvothe.batchms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.kvothe.batchms.entity.TransactionReport;
import tech.kvothe.batchms.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionReport>> listAll() {
        return ResponseEntity.ok(transactionService.getAllTransactionGroupByBusinessName());
    }

}
