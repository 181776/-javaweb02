package codeking.config;

import codeking.fliter.TokenFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注册 JWT 校验过滤器。使用 {@link FilterRegistrationBean} 与 Spring 生命周期一致；
 * 也可改用 Boot 4 的 {@code org.springframework.boot.web.server.servlet.context.ServletComponentScan}
 * 扫描带 {@code @WebFilter} 的类。
 */
@Configuration
public class TokenFilterConfig {

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterRegistration() {
        FilterRegistrationBean<TokenFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new TokenFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}
