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
                .antMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("supervisor")
                .antMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("supervisor")

                // Clients - Create and Update (SUPERVISOR ONLY)
                .antMatchers(HttpMethod.POST, "/api/clients/**").hasAuthority("supervisor")
                .antMatchers(HttpMethod.PUT, "/api/clients/**").hasAuthority("supervisor")

                // Inventory - Create and Update (SUPERVISOR ONLY)
                .antMatchers(HttpMethod.POST, "/api/inventory/**").hasAuthority("supervisor")
                .antMatchers(HttpMethod.PUT, "/api/inventory/**").hasAuthority("supervisor")

                // Orders - Create (SUPERVISOR ONLY)
                .antMatchers(HttpMethod.POST, "/api/order/create").hasAuthority("supervisor")

                // Orders - View (BOTH supervisor and operator)
                .antMatchers(HttpMethod.GET, "/api/order/**").hasAnyAuthority("supervisor", "operator")

                // General Rules (for GET - both supervisor and operator can view)
                .antMatchers("/api/clients/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/products/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/inventory/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/reports/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/invoice/**").hasAnyAuthority("supervisor", "operator")
                .antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")


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

//                .antMatchers(HttpMethod.GET, "/api/products/get-by**").hasAuthority("operator")
//                .antMatchers( "/api/clients").hasAuthority("supervisor")
//                .antMatchers("/api/products").hasAuthority("supervisor")
////                .antMatchers("/api/order").hasAuthority("supervisor")
//                .antMatchers("/api/order").permitAll()
//                .antMatchers("/api/inventory/**").permitAll()
//                .antMatchers("/api/reports/**").permitAll()
//                .antMatchers("/api/invoice/**").hasAnyAuthority("supervisor", "operator")
//                .antMatchers("/api/**").hasAnyAuthority("supervisor", "operator")//