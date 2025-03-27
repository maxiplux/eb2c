package app.quantun.eb2c.config.openapidocs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI 3.0 documentation.
 *
 * This class sets up the OpenAPI documentation for the application, including security schemes and API information.
 * The configurations are based on the module name and API version provided in the application properties.
 */
@Configuration
public class OpenApi30Config {

    private final String moduleName;
    private final String apiVersion;

    /**
     * Constructor to initialize module name and API version.
     *
     * @param moduleName the name of the module
     * @param apiVersion the version of the API
     */
    public OpenApi30Config(
            @Value("${module-name}") String moduleName,
            @Value("${api-version}") String apiVersion) {
        this.moduleName = moduleName;
        this.apiVersion = apiVersion;
    }

    /**
     * Bean to configure the OpenAPI documentation.
     *
     * This method sets up the OpenAPI documentation with a security scheme for bearer authentication and
     * includes the module name and API version in the API title.
     *
     * @return OpenAPI instance with the configured settings
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize(moduleName));
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(new Info().title(apiTitle).version(apiVersion));
    }
}
