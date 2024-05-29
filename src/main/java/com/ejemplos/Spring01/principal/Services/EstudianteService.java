package com.ejemplos.Spring01.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ejemplos.Spring01.principal.Models.Est;
import com.ejemplos.Spring01.principal.Repositories.EstudianteRepositorio;

@Service
public class EstudianteService {
	@Autowired
	private EstudianteRepositorio estudianteRepositorio;
	
	public List<Est> listar(){
		return estudianteRepositorio.findAll();
	}
	public List<Est> getPorApellido(String apellido){
		return estudianteRepositorio.findByApellido(apellido);
	}
}
