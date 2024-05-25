package com.ejemplos.Spring01.principal.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ejemplos.Spring01.principal.Models.Persona;

@Controller
@RequestMapping("/home") //localhost:7001/home
public class HomeController {
	@GetMapping // localhost:7001/home [GET]
	public String index() {
		return "home/index";
	}
	@PostMapping // localhost:7001/home [POST]
	public String prueba(@RequestBody Persona p) {
		System.out.printf("Nombre completo: %s %s, edad: %d%n",p.getNombre(),p.getApellido(),p.getEdad());
		return "yes";
	}
	@GetMapping("/algo/{a}")// localhost:7001/home/algo/pepe [GET]
	public void prueba2(@PathVariable("a") String a) {
		System.out.println("Valor unico desde path variable: "+ a);
	}
}
