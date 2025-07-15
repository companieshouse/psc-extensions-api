package uk.gov.companieshouse.psc.extensions.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.filter.CustomCorsFilter;
import uk.gov.companieshouse.psc.extensions.api.interceptor.RequestLifecycleInterceptor;

import java.util.List;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static uk.gov.companieshouse.psc.extensions.api.PscExtensionsApiApplication.APPLICATION_NAMESPACE;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig implements WebMvcConfigurer {

  private final SecurityProperties securityProperties;
  private final RequestLifecycleInterceptor loggingInterceptor;

  public WebSecurityConfig(final SecurityProperties securityProperties,
      final RequestLifecycleInterceptor loggingInterceptor) {
    this.securityProperties = securityProperties;
    this.loggingInterceptor = loggingInterceptor;
  }

  @Override
  public void addInterceptors(@NonNull final InterceptorRegistry registry) {
    registry.addInterceptor(loggingInterceptor);
    registry.addInterceptor(new InternalUserInterceptor(APPLICATION_NAMESPACE));
  }

  @Bean
  public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
    return http.cors(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(new CustomCorsFilter(List.of(GET.name())), CsrfFilter.class)
        .authorizeHttpRequests(request -> request
            .requestMatchers(POST, securityProperties.getApiSecurityPath()).permitAll()
            .requestMatchers(GET, securityProperties.getHealthcheckPath()).permitAll()
            .anyRequest().denyAll()
        ).build();

  }

}
