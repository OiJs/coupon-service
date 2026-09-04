package com.example.book2onandoncouponservice.config;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CouponExpireJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    private static final int CHUNK_SIZE = 1000;

    @Bean
    public Job couponExpireJob() {
        return new JobBuilder("couponExpireJob", jobRepository)
                .start(couponExpireStep())
                .build();
    }

    @Bean
    public Step couponExpireStep() {
        return new StepBuilder("couponExpireStep", jobRepository)
                .<Long, Long>chunk(CHUNK_SIZE, transactionManager)
                .reader(couponExpireReader())
                .writer(couponExpireWriter())
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Long> couponExpireReader() {
        return new JdbcCursorItemReaderBuilder<Long>()
                .name("couponExpireReader")
                .dataSource(dataSource)
                .sql("SELECT member_coupon_id FROM member_coupon WHERE member_coupon_end_date < ? AND member_coupon_status = 'NOT_USED'")
                .queryArguments(Timestamp.valueOf(LocalDateTime.now()))
                .rowMapper((rs, rowNum) -> rs.getLong("member_coupon_id"))
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Long> couponExpireWriter() {
        return new JdbcBatchItemWriterBuilder<Long>()
                .dataSource(dataSource)
                .sql("UPDATE member_coupon SET member_coupon_status = 'EXPIRED' WHERE member_coupon_id = :id")
                .itemSqlParameterSourceProvider(item -> new MapSqlParameterSource("id", item))
                .assertUpdates(false)
                .build();
    }
}