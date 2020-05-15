package com.foo.bar.batchexample;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.tools.Server;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import com.foo.bar.batchexample.item.Processor;
import com.foo.bar.batchexample.model.Caisse;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

  private static final String QUERY_FIND_CAISSES =
      "SELECT id, caisse, codeService FROM Caisse ORDER BY caisse ASC";

  @Autowired
  public JobBuilderFactory jobBuilderFactory;

  @Autowired
  public StepBuilderFactory stepBuilderFactory;

  @Autowired
  private DataSource dataSource;

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server inMemoryH2DatabaseaServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
  }

  @StepScope
  @Bean
  public JdbcCursorItemReader<Caisse> reader() {
    JdbcCursorItemReader<Caisse> databaseReader = new JdbcCursorItemReader<>();

    databaseReader.setDataSource(dataSource);
    databaseReader.setSql(QUERY_FIND_CAISSES);
    databaseReader.setRowMapper(new BeanPropertyRowMapper<Caisse>(Caisse.class));

    return databaseReader;
  }

  @StepScope
  @Bean
  public Processor processor() {
    return new Processor();
  }

  @StepScope
  @Bean
  public FlatFileItemWriter<Caisse> writer(
      @Value("#{jobParameters[outputPath]}") String outputPath) {
    FileSystemResource outputResource = new FileSystemResource(outputPath);
    FlatFileItemWriter<Caisse> writer = new FlatFileItemWriter<>();
    writer.setResource(outputResource);
    writer.setLineAggregator(new DelimitedLineAggregator<Caisse>() {
      {
        setDelimiter(",");
        setFieldExtractor(new BeanWrapperFieldExtractor<Caisse>() {
          {
            setNames(new String[] {"id", "caisse", "codeService"});
          }
        });
      }
    });
    return writer;
  }

  @Bean
  public Step step(ItemWriter<Caisse> writer) {
    return stepBuilderFactory.get("step").<Caisse, Caisse>chunk(10).reader(reader())
        .processor(processor()).writer(writer).build();
  }

  @Bean
  public Job job(final Step step) {
    return jobBuilderFactory.get("job").flow(step).end().build();
  }
}
