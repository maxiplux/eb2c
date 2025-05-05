package app.quantun.eb2c;


import app.quantun.eb2c.repository.*;
import app.quantun.eb2c.rest.UserController;
import app.quantun.eb2c.service.CognitoUserService;
import app.quantun.eb2c.service.impl.CognitoUserServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
})
@ComponentScan(basePackages = "app.quantun.eb2c",
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = BootstrapDataService.class
                ),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "app.quantun.eb2c.config.security.*"
                )
        }
)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public JpaMetamodelMappingContext jpaMetamodelMappingContext() {
        return Mockito.mock(JpaMetamodelMappingContext.class);
    }


    @Bean
    public UserController userController(CognitoUserServiceImpl userService) {
        return new UserController(userService);
    }

    @Bean
    public CategoryRepository categoryRepository() {
        return Mockito.mock(CategoryRepository.class);
    }

    @Bean
    public ProductRepository productRepository() {
        return Mockito.mock(ProductRepository.class);
    }


    // Add missing repositories needed by OrderController and other components
    @Bean
    public OrderRepository orderRepository() {
        return Mockito.mock(OrderRepository.class);
    }

    @Bean
    public BranchRepository branchRepository() {
        return Mockito.mock(BranchRepository.class);
    }

    @Bean
    public OrganizationRepository organizationRepository() {
        return Mockito.mock(OrganizationRepository.class);
    }


    // If there's a CognitoUserService dependency, add:
    @Bean
    public CognitoUserService cognitoUserService() {
        return Mockito.mock(CognitoUserServiceImpl.class);
    }


} 
