package tech.kvothe.batchms.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Nested;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import tech.kvothe.batchms.dto.Transaction;
import tech.kvothe.batchms.dto.TransactionCNAB;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class BatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(ItemReader<TransactionCNAB> itemReader,
              ItemProcessor<TransactionCNAB, Transaction> itemProcessor,
              ItemWriter<Transaction> itemWriter) {
        return new StepBuilder("step", jobRepository)
                .<TransactionCNAB, Transaction>chunk(1000, transactionManager)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @StepScope
    @Bean
    FlatFileItemReader<TransactionCNAB> reader(@Value("#{jobParameters['cnabFile']}") Resource resource) {
        return new FlatFileItemReaderBuilder<TransactionCNAB>()
                .name("reader")
                .resource(resource)
                .fixedLength()
                .columns(
                        new Range(1, 1),
                        new Range(2, 9),
                        new Range(10, 19),
                        new Range(20, 30),
                        new Range(31, 42),
                        new Range(43, 48),
                        new Range(49, 62),
                        new Range(63, 80)
                )
                .names("tipo", "data", "valor", "cpf",
                        "cartao", "hora", "donoDaLoja", "nomeDaLoja")
                .targetType(TransactionCNAB.class)
                .build();
    }

    @Bean
    ItemProcessor<TransactionCNAB, Transaction> processor() {
        return item -> {
            var transaction = new Transaction(
                    null,
                    item.tipo(),
                    null,
                    item.valor(),
                    item.cpf(),
                    item.cartao(),
                    null,
                    item.donoDaLoja().trim(),
                    item.nomeDaLoja().trim())
                    .withValue(item.valor().divide(BigDecimal.valueOf(100)))
                    .withDate(item.data())
                    .withHour(item.hora());
            return transaction;
        };
    }

    @Bean
    JdbcBatchItemWriter<Transaction> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transaction>()
                .dataSource(dataSource)
                .sql("""
                        INSERT INTO transacao (
                        tipo, data, valor, cpf, cartao,
                        hora, dono_loja, nome_loja
                        ) VALUES (
                        :tipo, :data, :valor, :cpf, :cartao,
                        :hora, :donoDaLoja, :nomeDaLoja
                        )
                        """)
                .beanMapped()
                .build();
    }

    @Bean
    JobLauncher jobLauncherAsync() throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }
}