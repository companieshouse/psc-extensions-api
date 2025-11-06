package uk.gov.companieshouse.psc.extensions.api.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.function.Supplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.csrf.CsrfFilter;
import uk.gov.companieshouse.api.filter.CustomCorsFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer  {

  private static final Supplier<List<String>> externalMethods = () -> List.of( GET.name() );


  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(new CustomCorsFilter(externalMethods.get() ), CsrfFilter.class )
        .authorizeHttpRequests(request -> request
            .requestMatchers(POST, "/transactions/*/persons-with-significant-control-extensions")
            .permitAll()
            .requestMatchers(GET, "/persons-with-significant-control-extensions/healthcheck")
            .permitAll()
            .requestMatchers(GET, "/persons-with-significant-control-extensions/*/extensionCount")
            .permitAll()
            .requestMatchers(GET, "/persons-with-significant-control-extensions/*/*/isPscExtensionRequestValid")
            .permitAll()
            .requestMatchers(GET, "/private/transactions/*/persons-with-significant-control-extensions/*/filings")
            .permitAll()
            .requestMatchers(GET, "/transactions/*/persons-with-significant-control-extensions/*/validation_status")
            .permitAll()
            .anyRequest()
            .denyAll());

    return http.build();

  }

}
