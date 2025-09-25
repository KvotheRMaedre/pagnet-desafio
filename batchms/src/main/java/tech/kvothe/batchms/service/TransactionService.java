package tech.kvothe.batchms.service;

import org.springframework.stereotype.Service;
import tech.kvothe.batchms.entity.Transaction;
import tech.kvothe.batchms.entity.TransactionReport;
import tech.kvothe.batchms.entity.TransactionType;
import tech.kvothe.batchms.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionReport> getAllTransactionGroupByBusinessName() {
        var transactions = transactionRepository.findAllByOrderByNomeDaLojaAscIdDesc();

        var reportMap = new LinkedHashMap<String, TransactionReport>();
        transactions.forEach( transaction -> {
            var nomeDaLoja = transaction.nomeDaLoja();
            var valorTransacao = transaction.valor();

            reportMap.compute(nomeDaLoja, (key, existingReport) -> {
                var report  = (existingReport != null) ? existingReport :
                        new TransactionReport(key, BigDecimal.ZERO, new ArrayList<>());

                return report
                        .addTotal(valorTransacao)
                        .addTransaction(transaction);
            });
        });
        return new ArrayList<>(reportMap.values());
    }
}
