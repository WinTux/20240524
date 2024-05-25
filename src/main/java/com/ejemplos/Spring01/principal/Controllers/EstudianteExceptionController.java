package com.ejemplos.Spring01.principal.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ejemplos.Spring01.principal.Exceptions.EstudianteNoEncontradoException;

@ControllerAdvice
public class EstudianteExceptionController {
	@ExceptionHandler(value=EstudianteNoEncontradoException.class)
	public ResponseEntity<Object> unaExcepcionDeEstudiante(EstudianteNoEncontradoException ex){
		return new ResponseEntity<>("NO SE ENCONTRÓ AL ESTUDIANTE",HttpStatus.NOT_FOUND);
	}
}
