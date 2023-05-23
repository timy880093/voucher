package com.gateweb.voucher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class I18nConfig {

  @Bean
  public LocalValidatorFactoryBean localValidator() {
    final LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
    // bean.setProviderClass(HibernateValidator.class);
    bean.setValidationMessageSource(getMessageSource());
    return bean;
  }

  @Bean
  public ReloadableResourceBundleMessageSource getMessageSource() {
    final ReloadableResourceBundleMessageSource messageSource =
        new ReloadableResourceBundleMessageSource();
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setUseCodeAsDefaultMessage(true);
    // "ValidationMessages" is for validation
    messageSource.setBasenames("classpath:static/Errorcode", "Errorcode");
    messageSource.setCacheSeconds(600); // reload messages every 10 min
    return messageSource;
  }
}
