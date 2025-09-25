package tech.kvothe.batchms.entity;

import java.math.BigDecimal;
import java.util.List;

public record TransactionReport(
        String nomeDaLoja,
        BigDecimal total,
        List<Transaction> transacoes) {

    public TransactionReport addTotal(BigDecimal value) {
        return new TransactionReport(nomeDaLoja, total.add(value), transacoes);
    }

    public TransactionReport addTransaction(Transaction transaction) {
        transacoes.add(transaction);
        return new TransactionReport(nomeDaLoja, total, transacoes);
    }
}
