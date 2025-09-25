package tech.kvothe.batchms.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Table(name = "transacao")
public record Transaction(
        @Id Long id,
        Integer tipo,
        Date data,
        BigDecimal valor,
        Long cpf,
        String cartao,
        Time hora,
        @Column("dono_loja") String donoDaLoja,
        @Column("nome_loja") String nomeDaLoja) {

    public Transaction withValue(BigDecimal valor) {
        return new Transaction(
                id,
                tipo,
                data,
                valor,
                cpf,
                cartao,
                hora,
                donoDaLoja,
                nomeDaLoja);
    }

    public Transaction withDate(String data) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(data);

        return new Transaction(
                id,
                tipo,
                new Date(date.getTime()),
                valor,
                cpf,
                cartao,
                hora,
                donoDaLoja,
                nomeDaLoja);
    }

    public Transaction withHour(String hora) throws ParseException {
        var dateFormat = new SimpleDateFormat("HHmmss");
        var hour = dateFormat.parse(hora);

        return new Transaction(
                id,
                tipo,
                data,
                valor,
                cpf,
                cartao,
                new Time(hour.getTime()),
                donoDaLoja,
                nomeDaLoja);
    }

}
