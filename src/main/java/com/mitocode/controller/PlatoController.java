package com.mitocode.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
	
	@PostMapping("/operar")
	public Mono<String> operarController(/*@Valid*/ Plato plato, /*BindingResult validaciones,*/ 
			Model model, SessionStatus status) {
		return service.registrarService(plato)
				.doOnNext(p -> log.info("Plato guardado: " + p.getNombre() + " Id: " + p.getId()))
				.thenReturn("redirect:/platos/listar");
	}
	
}
