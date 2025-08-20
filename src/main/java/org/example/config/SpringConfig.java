package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ComponentScan("org.example")
@PropertySources({
        @PropertySource(value="file:./example.properties",ignoreResourceNotFound = true)
})
public class SpringConfig {

}
