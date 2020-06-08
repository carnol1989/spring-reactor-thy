package com.mitocode.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.mitocode.document.Plato;
import com.mitocode.service.IPlatoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/platos")
@SessionAttributes("plato")
public class PlatoController {

	private static final Logger log = LoggerFactory.getLogger(PlatoController.class);
	
	@Autowired
	private IPlatoService service;
	
	@GetMapping("/listar")
	public Mono<String> listarController(Model model) {
		Flux<Plato> platos = service.listarService();
		platos.doOnNext(p -> log.info(p.getNombre())).subscribe();
		
		model.addAttribute("platos", platos);
		model.addAttribute("titulo", "Listado de Platos");
		
		return Mono.just("platos/listar");
	}
	
	@GetMapping("/form")
	public Mono<String> crearController(Model model) {
		model.addAttribute("plato", new Plato());
		model.addAttribute("titulo", "Formulario de Plato");
		model.addAttribute("boton", "Crear");
		return Mono.just("platos/form");
	}
	
	@GetMapping("/form/{id}")
	public Mono<String> editarController(@PathVariable String id, Model model) {
		Mono<Plato> platoMono = service.listarPorIdService(id)
				.doOnNext(p -> log.info("Plato: " + p.getNombre()))
				.defaultIfEmpty(new Plato());
		
		model.addAttribute("titulo", "Editar Plato");
		model.addAttribute("boton", "Editar");
		model.addAttribute("plato", platoMono);
		
		return Mono.just("platos/form");
	}
	
	@PostMapping("/operar")
	public Mono<String> operarController(@Valid Plato plato, BindingResult validaciones, 
			Model model, SessionStatus status) {
		if (validaciones.hasErrors()) {
			validaciones.reject("ERR780", "Error de validacion de formulario");
			model.addAttribute("titulo", "Errores en formulario de platos");
			model.addAttribute("boton", "Guardar");
			return Mono.just("platos/form");
		} else {
			//Limpiar variables de sesión
			status.setComplete();			
			return service.registrarService(plato)
					.doOnNext(p -> log.info("Plato guardado: " + p.getNombre() + " Id: " + p.getId()))
					.thenReturn("redirect:/platos/listar?success=plato+guardado");
		}		
	}
	
	@GetMapping("/eliminar/{id}")
	public Mono<String> eliminarController(@PathVariable String id) {
		return service.listarPorIdService(id).defaultIfEmpty(new Plato()) //Mono<Plato>
				//.then(Mono.just("redirect:/platos/listar"))//si el then está aquí, flujo termina en ese momento
				.flatMap(p -> {
					if (p.getId() == null) {
						return Mono.error(new InterruptedException("No existe id.")); //Mono<Object>
					}
					return Mono.just(p); //Mono<Plato>
				}).flatMap(p -> {
					log.info("A punto de eliminar el plato: " + p.getNombre());
					return service.eliminarService(p.getId()); //Mono<Void>
				}).then(Mono.just("redirect:/platos/listar"))
				.onErrorResume(ex -> Mono.just("redirect:/platos/listar?error=Error%20Interno")); //+ ex.getMessage()));
	}
	
}
