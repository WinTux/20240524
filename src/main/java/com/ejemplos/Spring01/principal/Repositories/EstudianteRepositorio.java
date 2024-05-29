package com.ejemplos.Spring01.principal.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejemplos.Spring01.principal.Models.Est;
@Repository
public interface EstudianteRepositorio extends JpaRepository<Est,Integer>{
	List<Est> findByApellido(String ap);
	List<Est> findByNombreAndApellido(String nom,String ap);
	
}
