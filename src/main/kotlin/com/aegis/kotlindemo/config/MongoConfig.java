/*
 * Copyright (c) 2018. authored by mcs
 */

package com.aegis.kotlindemo.config;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * 该Config类用于mongo插入时，避免增加_class字段
 */
@Configuration
public class MongoConfig {
  @Bean
  public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
    DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
    MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
    try {
      mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
    } catch (NoSuchBeanDefinitionException ignore) {
    }

    // Don't save _class to mongo
    mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

    return mappingConverter;
  }
}
