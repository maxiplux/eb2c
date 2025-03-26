package app.quantun.eb2c.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler successHandler;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private CustomOidcUserService customOidcUserService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Permit static resources and SAML endpoints
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/public/**",
                                "/media/**", "/custom-login", "/login", "/custom-logout",
                                "/login/oauth2/**",
                                "v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/app/login/**", "/app/logout/**",
                                "/", "/error/**",
                                "api/**", "/actuator/**",
                                "/saml2/**").permitAll()
                        .requestMatchers("/dashboard").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(saml2 -> saml2
                        // .loginPage("/custom-login")
                        .defaultSuccessUrl("/dashboard")
                        .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout/saml2/slo")
                        .logoutSuccessHandler(logoutSuccessHandler)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/custom-login")
                        .deleteCookies("JSESSIONID")
                );


        return http.build();
    }


}