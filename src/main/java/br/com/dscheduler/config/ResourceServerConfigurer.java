package br.com.dscheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfigurer extends ResourceServerConfigurerAdapter {

    @Value("${security.oauth2.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    @Value("${security.oauth2.resource.check-token-url}")
    private String checkTokenUrl;

    @Value("${security.oauth2.resource.resource-id}")
    private String resourceId;

    private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('read')";
    private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('write')";
    private static final String SECURED_PATTERN = "/**";
    private static final String PUBLIC_PATTERN = "/login/**";

    private static final String[] SWAGGER_UI = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    @Primary
    public ResourceServerTokenServices tokenServices() {

        RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setClientId(clientId);
        tokenServices.setClientSecret(clientSecret);
        tokenServices.setCheckTokenEndpointUrl(checkTokenUrl);

        return tokenServices;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServices()).resourceId(resourceId);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.cors().and().requestMatchers()
                .antMatchers(SECURED_PATTERN).and().authorizeRequests()
                .antMatchers(SWAGGER_UI).permitAll()
                .antMatchers(PUBLIC_PATTERN).permitAll()
                .antMatchers(HttpMethod.POST, SECURED_PATTERN).access(SECURED_WRITE_SCOPE)
                .anyRequest().access(SECURED_READ_SCOPE);
    }
}
