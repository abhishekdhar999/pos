package org.example.config;

import javax.servlet.Filter;
import org.springframework.web.WebApplicationInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Register your CORS filter
        javax.servlet.FilterRegistration.Dynamic corsFilter =
                servletContext.addFilter("corsFilter", (Filter) new SimpleCORSFilter());

        corsFilter.addMappingForUrlPatterns(null, false, "/*");
    }
}

