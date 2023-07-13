package Funssion.Inforum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig{

@Bean
public DataSource dataSource(){
    DriverManagerDataSource dataSource=new DriverManagerDataSource();
    dataSource.setUsername("sa");
    dataSource.setUrl("jdbc:h2:tcp://localhost/~/test;MODE=PostgreSQL;");
    return dataSource;
}

@Bean
public JdbcTemplate jdbcTemplate(){
    return new JdbcTemplate(dataSource());
    }
}