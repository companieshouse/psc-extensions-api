package uk.gov.companieshouse.psc.extensions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import uk.gov.companieshouse.psc.extensions.filter.UserAuthenticationFilter;
import uk.gov.companieshouse.api.filter.CustomCorsFilter;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final Supplier<List<String>> externalMethods = () -> List.of( GET.name() );

    @Bean
    public SecurityFilterChain filterChain( final HttpSecurity http ) throws Exception {
        http.cors( AbstractHttpConfigurer::disable )
                .sessionManagement( s -> s.sessionCreationPolicy( SessionCreationPolicy.STATELESS ) )
                .csrf( AbstractHttpConfigurer::disable )
                .addFilterBefore( new CustomCorsFilter( externalMethods.get() ), CsrfFilter.class )
                .addFilterAfter( new UserAuthenticationFilter(), CsrfFilter.class )
                .authorizeHttpRequests( request -> request
                        .requestMatchers( GET, "/psc-extensions-api/healthcheck" ).permitAll() //TODO: double check this
                        .anyRequest().denyAll()
                );
        return http.build();

    }

}
