package tech.kvothe.batchms.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record Transaction(
        Long id,
        Integer tipo,
        Date data,
        BigDecimal valor,
        Long cpf,
        String cartao,
        Time hora,
        String donoDaLoja,
        String nomeDaLoja) {

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
