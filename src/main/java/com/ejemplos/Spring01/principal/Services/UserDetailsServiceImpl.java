package com.ejemplos.Spring01.principal.Services;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ejemplos.Spring01.principal.Models.Est;
import com.ejemplos.Spring01.principal.Models.EstUserDetails;


@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private EstudianteService estServ;
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		logger.debug("Entrando al método loadUserByUsername (Services.UserDetailsServiceImpl)");
        Optional<Est> user = estServ.hallarPorEmail(email);
        if(user == null){
            logger.error("No se encontro al estudiante: " + email);
            throw new UsernameNotFoundException("No se pudo encontrar al usuario!");
        }
        logger.info("Usuario autenticado satisfactoriamente");
        return new EstUserDetails(user.get());
	}
}
