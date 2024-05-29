package com.ejemplos.Spring01.principal.Filters;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
@Component
@Order(1)
public class UnFiltro implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Host remoto: "+request.getRemoteHost());
		System.out.println("Address remoto: "+request.getRemoteAddr());
		chain.doFilter(request, response);
	}
}
