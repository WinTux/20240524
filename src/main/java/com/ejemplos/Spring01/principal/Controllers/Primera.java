package com.ejemplos.Spring01.principal.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Primera {
	@Value("${valor.secreto}")
	private String mensaje;
	
	@RequestMapping("/")
	@ResponseBody
	public String saludo() {
		return "Hola a todos. "+ mensaje;
	}
}
