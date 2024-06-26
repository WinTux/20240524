package com.ejemplos.Spring01.principal.Controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ejemplos.Spring01.principal.Exceptions.EstudianteNoEncontradoException;
import com.ejemplos.Spring01.principal.Models.Est;
import com.ejemplos.Spring01.principal.Models.Estudiante;
import com.ejemplos.Spring01.principal.Models.PeticionAuth;
import com.ejemplos.Spring01.principal.Models.RespuestaAuthJwt;
import com.ejemplos.Spring01.principal.Services.EstudianteService;
import com.ejemplos.Spring01.principal.Services.JwtService;
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
	@Autowired
	private EstudianteService estudianteService;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtService jwtService;
	private static Map<String, Estudiante> estudiantes = new HashMap<>();
	static {
		Estudiante e1 = new Estudiante(1, "Pepe","Perales");
		Estudiante e2 = new Estudiante(2, "Ana","Sosa");
		Estudiante e3 = new Estudiante(3, "Sofía","Rocha");
		estudiantes.put("1", e1);
		estudiantes.put("2", e2);
		estudiantes.put("3", e3);
	}
	@PostMapping("/login")
	public ResponseEntity<RespuestaAuthJwt> AuthenticateAndGetToken(@RequestBody PeticionAuth authRequestDTO){
	    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsuario(), authRequestDTO.getPassword()));
	    if(authentication.isAuthenticated()){
	    	RespuestaAuthJwt r = new RespuestaAuthJwt();
	    	r.setToken(jwtService.GenerateToken(authRequestDTO.getUsuario()));
	       return new ResponseEntity<>(r, HttpStatus.OK);
	    } else {
	        throw new UsernameNotFoundException("Peticion incorrecta");
	    }
	}
	@GetMapping("/estudiante")// localhost:7001/estudiante [GET]
	public ResponseEntity<Object> getEstudiantes(){
		List<Est> ests = estudianteService.listar();
		return new ResponseEntity<>(ests,HttpStatus.OK);
		//return new ResponseEntity<>(estudiantes.values(),HttpStatus.OK);
	}
	@PostMapping("/estudiante")//localhost:7001/estudiante [POST]
	public ResponseEntity<Object> nuevoEst(@RequestBody Est est){
		estudianteService.registrar(est);
		URI ubicacionDelRecurso = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(est.getMatricula())
				.toUri();
		//estudiantes.put(est.getId()+"", est);
		return ResponseEntity.created(ubicacionDelRecurso).build();
	}
	@GetMapping("/estudiante/estado")//localhost:7001/estudiante/estado [GET]
	public ResponseEntity<String> getEstado(){
		return ResponseEntity.ok("Controlador de estudiante funcionando.");
	}
	@PutMapping("/estudiante/{id}")//localhost:7001/estudiante/5 [PUT]
	public ResponseEntity<Object> modificarEstudiante(@PathVariable("id") int id, @RequestBody Est est){
		Optional<Est> estudiante = estudianteService.hallarEstudiante(id);
		if(!estudiante.isPresent())
			throw new EstudianteNoEncontradoException();
		Est estDDBB = estudiante.get();
		estDDBB.setNombre(est.getNombre());
		estDDBB.setApellido(est.getApellido());
		estDDBB.setFechanacimiento(est.getFechanacimiento());
		estDDBB.setCarreraid(est.getCarreraid());
		estDDBB.setPassword(est.getPassword());
		estDDBB.setEmail(est.getEmail());
		estDDBB.setEstado(est.getEstado());
		estudianteService.registrar(estDDBB);
		/*
		estudiantes.remove(id);
		est.setId(id);
		estudiantes.put(id+"", est);
		*/
		return new ResponseEntity<>("Se actualizaron los datos del estudiante "+id, HttpStatus.OK);
	}
	@DeleteMapping("/estudiante/{id}")//localhost:7001/estudiante/2 [DELETE]
	public ResponseEntity<Object> elminarEstudiante(@PathVariable("id") int id){
		Optional<Est> estudiante = estudianteService.hallarEstudiante(id);
		if(!estudiante.isPresent())
			throw new EstudianteNoEncontradoException();
		Est estDDBB = estudiante.get();
		estudianteService.eliminar(estDDBB);
		//estudiantes.remove(id+"");
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
	@PostMapping(path="/estudiante/subida",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> archivoSubir(@RequestParam("archivito") MultipartFile arch) throws IOException{
		File elArchivo = new File("/Users/rusokverse/Desktop/"+ arch.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(elArchivo);
		fos.write(arch.getBytes());
		return ResponseEntity.ok("El archivo se recibió correctamente.");
	}
	@GetMapping(path="/estudiante/bajada")
	public ResponseEntity<Object> descargarArchivo() throws IOException{
		String nombreArch = "/Users/rusokverse/Desktop/mapache-1.jpg";
		File archivo = new File(nombreArch);
		InputStreamResource recurso = new InputStreamResource(new FileInputStream(archivo));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma","no-cache");
		headers.add("Expires", "0");
		ResponseEntity<Object> re = ResponseEntity.ok()
				.headers(headers).contentLength(archivo.length())
				.contentType(MediaType.parseMediaType("application/txt"))
				.body(recurso);
		return re;
	}
	@GetMapping(value="/estudiante/{apellido}")// https://localhost:7001/estudiante/Rocha [GET]
	public ResponseEntity<Object> getEstudiantePorAp(@PathVariable("apellido") String apellido){
		List<Est> ests = estudianteService.getPorApellido(apellido);
		return new ResponseEntity<>(ests, HttpStatus.OK);
	}
}
