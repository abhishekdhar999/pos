package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http//

                .csrf().disable()
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")//
                .antMatchers("/api/**", "/session/**")//  // ✅ Add session endpoints
                .and().authorizeRequests()//
                .antMatchers("/session/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/products/get-by**").hasAuthority("operator")
                .antMatchers( "/api/clients").hasAuthority("supervisor")
                .antMatchers("/api/products").hasAuthority("supervisor")
//                .antMatchers("/api/order").hasAuthority("supervisor")
                .antMatchers("/api/order").permitAll()
                .antMatchers("/api/inventory").permitAll()
                .antMatchers("/api/invoice/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")//
                // Ignore CSRF and CORS
                .and()
                .cors();


    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}

//http
//        .csrf().disable()
//                .cors() // enables CorsConfigurationSource
//                .and()
//                .authorizeRequests()
//                .antMatchers("/session/**").permitAll()
//                .antMatchers("/api/**").authenticated();