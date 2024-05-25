package com.ejemplos.Spring01.principal.Controllers;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.ejemplos.Spring01.principal.Exceptions.EstudianteNoEncontradoException;
import com.ejemplos.Spring01.principal.Models.Estudiante;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class EstudianteController {
	
	private static final Logger logger = LoggerFactory.getLogger(EstudianteController.class);
	@Autowired
	ObjectMapper objectMapper;
	
	private static Map<String, Estudiante> estudiantes = new HashMap<>();
	static {
		Estudiante e1 = new Estudiante(1, "Pepe","Perales");
		Estudiante e2 = new Estudiante(2, "Ana","Sosa");
		Estudiante e3 = new Estudiante(3, "Sofía","Rocha");
		estudiantes.put("1", e1);
		estudiantes.put("2", e2);
		estudiantes.put("3", e3);
	}
	@GetMapping("/estudiante")// localhost:7001/estudiante [GET]
	public ResponseEntity<Object> getEstudiantes(){
		return new ResponseEntity<>(estudiantes.values(),HttpStatus.OK);
	}
	@PostMapping("/estudiante")//localhost:7001/estudiante [POST]
	public ResponseEntity<Object> nuevoEst(@RequestBody Estudiante est){
		estudiantes.put(est.getId()+"", est);
		return new ResponseEntity<>("Se creó el estudiante "+est.getId(), HttpStatus.CREATED);
	}
	@GetMapping("/estudiante/estado")//localhost:7001/estudiante/estado [GET]
	public ResponseEntity<String> getEstado(){
		return ResponseEntity.ok("Controlador de estudiante funcionando.");
	}
	@PutMapping("/estudiante/{id}")//localhost:7001/estudiante/5 [PUT]
	public ResponseEntity<Object> modificarEstudiante(@PathVariable("id") int id, @RequestBody Estudiante est){
		estudiantes.remove(id);
		est.setId(id);
		estudiantes.put(id+"", est);
		return new ResponseEntity<>("Se actualizaron los datos del estudiante "+id, HttpStatus.OK);
	}
	@DeleteMapping("/estudiante/{id}")//localhost:7001/estudiante/2 [DELETE]
	public ResponseEntity<Object> elminarEstudiante(@PathVariable("id") int id){
		estudiantes.remove(id+"");
		logger.info("Se eliminó correctamente "+ id);
		return new ResponseEntity<>("Se eliminó al estudiante "+id, HttpStatus.OK);
	}
	//Ejemplo para BadRequest y Ok
	@GetMapping("/estudiante/edad")// localhost:7001/estudiante/edad?nacimiento=2001 [GET]
	public ResponseEntity<String> getEdad(@RequestParam("nacimiento") int nacimiento){
		if(estaEnElFuturo(nacimiento)) {
			return ResponseEntity.badRequest()
					.body("El año de nacimiento no puede estar en el futuro ("+ nacimiento+")");
		}
		LocalDate fechaActual = LocalDate.now();
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		return ResponseEntity
				.status(HttpStatus.OK)
				.header("Fecha-Actual", fechaActual.format(formateador))
				.header("Anyo-Nacimiento", nacimiento+"")
				.body("La edad es de "+ calcularEdad(nacimiento));
	}
	private boolean estaEnElFuturo(int year) {
		return year > java.time.Year.now().getValue();
	}
	private int calcularEdad(int year) {
		return java.time.Year.now().getValue() - year;
	}
	/*
	@GetMapping("/pruebacruda")
	public void ejemploCrudo(HttpServletResponse response) {
		response.setHeader("Codigo-Depuracion", "XZ-400");
		response.setStatus(200);
		response.getWriter().println("Exito");
	}
	*/
	@PatchMapping("/estudiante/{id}")
	public ResponseEntity<String> editarconPatch(@PathVariable("id") int id, @RequestBody Map<String,Object> atributosModificados){
		if(!estudiantes.containsKey(id+""))
			throw new EstudianteNoEncontradoException();
		Estudiante estOriginal = estudiantes.get(id+"");
		atributosModificados.forEach(
				(atributo, valorNuevo)->{
					Field campo = ReflectionUtils.findField(Estudiante.class, atributo);
					if(campo!=null) {
						campo.setAccessible(true);
						ReflectionUtils.setField(campo, estOriginal, valorNuevo);
					}
				}
		);
		estudiantes.remove(id+"");
		estudiantes.put(id+"", estOriginal);
		return new ResponseEntity<>("Se modificó el estudiante (Patch)", HttpStatus.OK);
	}
	@PatchMapping(path="/estudiante/patch/{id}", consumes="application/json-patch+json")
	public ResponseEntity<String> editatrConJsonPatch(@PathVariable("id") String id, @RequestBody JsonPatch atributosModificados){
		try {
			Estudiante estOriginal = estudiantes.get(id);
			JsonNode patcheado = atributosModificados.apply(objectMapper.convertValue(estOriginal, JsonNode.class));
			Estudiante estActualizado = objectMapper.treeToValue(patcheado, Estudiante.class);
			estudiantes.remove(id);
			estudiantes.put(id, estActualizado);
			return new ResponseEntity<>("Se modificó exitosamente (JSON Patch)",HttpStatus.OK);
		} catch (IllegalArgumentException | JsonPatchException | JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>("No se modificó exitosamente (JSON Patch)",HttpStatus.INTERNAL_SERVER_ERROR);
		}//Injeccion de dependencias
	}
	// Excepciones
}
