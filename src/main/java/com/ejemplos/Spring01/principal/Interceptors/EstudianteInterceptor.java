package com.ejemplos.Spring01.principal.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class EstudianteInterceptor implements HandlerInterceptor{
	private static final Logger logger = LoggerFactory.getLogger(EstudianteInterceptor.class);
	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception{
		if(request.getMethod().equalsIgnoreCase("PUT")) {
			logger.info(String.format("Usuario %s, host %s desde la dirección %s:%d editará al estudiante %s (%s)",
					request.getRemoteUser(),
					request.getRemoteHost(),
					request.getRemoteAddr(),
					request.getRemotePort(),
					request.getRequestURI().split("/")[2],
					request.getRequestURI()
					));
			/*
			 * /estudiante/123
			 * ["", "estudiante", "123"]
			 * */
		}
		return true;
	}
	@Override
	public void postHandle(HttpServletRequest request,HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception{
		
	}
}
