package app.quantun.eb2c.config;


import app.quantun.eb2c.config.logging.CorrelationIdUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import static app.quantun.eb2c.config.logging.CorrelationIdConstants.CORRELATION_ID_HEADER_NAME;


/**
 * Configuration for RestClient to propagate correlation IDs to downstream services.
 */
@Configuration
public class RestClientConfig {


    /**
     * Creates a RestClient.Builder bean that automatically adds the correlation ID
     * to outgoing requests if it exists in the MDC.
     *
     * @return the configured RestClient.Builder
     */
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInitializer(request -> {
                    // Get the correlation ID from the MDC
                    String correlationId = CorrelationIdUtils.getCurrentCorrelationId();

                    // If the correlation ID exists, add it to the request header
                    if (correlationId != null && !correlationId.isEmpty()) {
                        request.getHeaders().add(CORRELATION_ID_HEADER_NAME, correlationId);
                    }
                });
    }
}
