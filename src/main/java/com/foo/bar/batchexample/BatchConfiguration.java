package com.foo.bar.batchexample;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.tools.Server;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.foo.bar.batchexample.item.Processor;
import com.foo.bar.batchexample.item.Writer;
import com.foo.bar.batchexample.model.Caisse;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private static final String QUERY_FIND_CAISSES = "SELECT " +
            "caisse, " +
            "codeService, " +
            "FROM Caisse " +
            "ORDER BY caisse ASC";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseaServer() throws SQLException {
        return Server.createTcpServer(
                "-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
    }

    @Bean
    ItemReader<Caisse> reader() {
        JdbcCursorItemReader<Caisse> databaseReader = new JdbcCursorItemReader<>();

        databaseReader.setDataSource(dataSource);
        databaseReader.setSql(QUERY_FIND_CAISSES);
        databaseReader.setRowMapper(new BeanPropertyRowMapper<Caisse>(Caisse.class));

        return databaseReader;
    }

    @Bean
    public Processor processor() {
        return new Processor();
    }

    @Bean
    public Writer writer() {
        return new Writer();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                .<Caisse, Caisse> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(
            final Step step) {
        return jobBuilderFactory.get("job")
                .flow(step)
                .end()
                .build();
    }
}
