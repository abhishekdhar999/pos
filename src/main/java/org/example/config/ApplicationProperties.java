package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ApplicationProperties {
    @Value("${invoice.baseUrl}")
    private String invoiceBaseUrl;
    @Value("${supervisor.email}")
    private String supervisorEmail;

}
