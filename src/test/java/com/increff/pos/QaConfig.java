package com.increff.pos;

import org.example.config.SpringConfig;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableScheduling
@ComponentScan(
        basePackages = { "org.example" }, //
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { SpringConfig.class })
)
@PropertySources({
        @PropertySource(value = "classpath:test.properties", ignoreResourceNotFound = true)
})
public class QaConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}

