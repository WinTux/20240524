package com.ejemplos.Spring01.principal.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ejemplos.Spring01.principal.Filters.JwtAuthFilter;
import com.ejemplos.Spring01.principal.Filters.UnFiltro;
import com.ejemplos.Spring01.principal.Services.UserDetailsServiceImpl;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class ConfiguracionDeSeguridad {
	@Autowired
	private UnFiltro filtro;
	@Autowired
    JwtAuthFilter jwtAuthFilter;
	@Bean
	FilterRegistrationBean<Filter> miFilterRegBean(){
		FilterRegistrationBean<Filter> fil = new FilterRegistrationBean<>();
		fil.setFilter(filtro);
		fil.addUrlPatterns("/estudiante/*");
		fil.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return fil;
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	    return http.csrf(x->x.disable())
	    	.authorizeHttpRequests((au)-> au
	    			.requestMatchers("/").permitAll()
	    			.requestMatchers("/home").permitAll()
	    			.requestMatchers("/login").permitAll()
	    			.requestMatchers("/estudiante/**").authenticated())
	    	.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	    	.authenticationProvider(authenticationProvider())
	  	    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	  	    .build();
	}
	@Bean
    public PasswordEncoder passwordEncoder() {
		return new PasswordTextoPlanoEncoder();
    }
	@Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }
	@Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }
	@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
