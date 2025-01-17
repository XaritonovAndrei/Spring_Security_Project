package com.ecom.store.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer dataInitializer = new DataSourceInitializer();
        dataInitializer.setDataSource(dataSource);
        ResourceDatabasePopulator dbPopulator = new ResourceDatabasePopulator();
        dbPopulator.addScript(new ClassPathResource("schema.sql"));
        dataInitializer.setDatabasePopulator(dbPopulator);
        return dataInitializer;
    }
}
