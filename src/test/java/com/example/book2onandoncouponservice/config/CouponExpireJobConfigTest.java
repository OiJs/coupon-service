package com.example.book2onandoncouponservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.transaction.PlatformTransactionManager;

@ExtendWith(MockitoExtension.class)
class CouponExpireJobConfigTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private PlatformTransactionManager transactionManager;
    @Mock
    private DataSource dataSource;

    @InjectMocks
    private CouponExpireJobConfig couponExpireJobConfig;

    @Test
    @DisplayName("Job Bean 생성 확인")
    void couponExpireJob() {
        Job job = couponExpireJobConfig.couponExpireJob();
        assertThat(job).isNotNull();
        assertThat(job.getName()).isEqualTo("couponExpireJob");
    }

    @Test
    @DisplayName("Step Bean 생성 확인")
    void couponExpireStep() {
        Step step = couponExpireJobConfig.couponExpireStep();
        assertThat(step).isNotNull();
        assertThat(step.getName()).isEqualTo("couponExpireStep");
    }

    @Test
    @DisplayName("Reader Bean 생성 확인")
    void couponExpireReader() {
        JdbcCursorItemReader<Long> reader = couponExpireJobConfig.couponExpireReader();
        assertThat(reader).isNotNull();
    }

    @Test
    @DisplayName("Writer Bean 생성 확인")
    void couponExpireWriter() {
        JdbcBatchItemWriter<Long> writer = couponExpireJobConfig.couponExpireWriter();
        assertThat(writer).isNotNull();
    }
}