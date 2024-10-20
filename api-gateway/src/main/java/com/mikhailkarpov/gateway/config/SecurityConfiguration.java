package com.mikhailkarpov.gateway.config;

import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

  private final ReactiveClientRegistrationRepository clientRegistrationRepository;
  private final String frontendUrl = "http://localhost:5173";

  @Autowired
  public SecurityConfiguration(ReactiveClientRegistrationRepository clientRegistrationRepository) {
    this.clientRegistrationRepository = clientRegistrationRepository;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {

    return serverHttpSecurity
        .authorizeExchange(authorizeExchangeSpec ->
            authorizeExchangeSpec
                .pathMatchers("/login/**", "/oauth2/**", "/logout").permitAll()
                .anyExchange().authenticated()
        )
        .csrf(CsrfSpec::disable)
        .cors(Customizer.withDefaults())
        .exceptionHandling(exceptionHandlingSpec ->
            exceptionHandlingSpec
                .authenticationEntryPoint(this.authenticationEntryPoint())
                .accessDeniedHandler(this.accessDeniedHandler())
        )
        .oauth2Login(oAuth2LoginSpec ->
            oAuth2LoginSpec.authenticationSuccessHandler(this.authenticationSuccessHandler())
        )
        .oauth2Client(Customizer.withDefaults())
        .logout(logoutSpec ->
            logoutSpec.logoutUrl("/logout")
                .logoutSuccessHandler(this.logoutSuccessHandler())
        )
        .build();
  }

  private HttpStatusServerEntryPoint authenticationEntryPoint() {

    return new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
  }

  private HttpStatusServerAccessDeniedHandler accessDeniedHandler() {

    return new HttpStatusServerAccessDeniedHandler(HttpStatus.FORBIDDEN);
  }

  private RedirectServerAuthenticationSuccessHandler authenticationSuccessHandler() {

    var authenticationSuccessHandler = new RedirectServerAuthenticationSuccessHandler();
    authenticationSuccessHandler.setLocation(this.getFrontendUrl());
    return authenticationSuccessHandler;
  }

  private OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler() {

    var logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(
        this.clientRegistrationRepository);
    logoutSuccessHandler.setPostLogoutRedirectUri(this.frontendUrl);
    return logoutSuccessHandler;
  }

  private URI getFrontendUrl() {
    try {
      return new URI(this.frontendUrl);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
