package com.ejemplos.Spring01.principal.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.ejemplos.Spring01.principal.Filters.UnFiltro;

import jakarta.servlet.Filter;

@Configuration
@EnableWebSecurity
public class ConfiguracionDeSeguridad {
	@Autowired
	private UnFiltro filtro;
	@Bean
	FilterRegistrationBean<Filter> miFilterRegBean(){
		FilterRegistrationBean<Filter> fil = new FilterRegistrationBean<>();
		fil.setFilter(filtro);
		fil.addUrlPatterns("/estudiante/*");
		fil.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return fil;
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http.requiresChannel(channel ->
			channel.anyRequest().requiresInsecure()
			).authorizeHttpRequests(authorize->
				authorize.anyRequest().permitAll()
			).build();
	}
}
